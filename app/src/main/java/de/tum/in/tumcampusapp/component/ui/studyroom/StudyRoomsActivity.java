package de.tum.in.tumcampusapp.component.ui.studyroom;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Collections;
import java.util.List;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.other.generic.activity.ActivityForLoadingInBackground;
import de.tum.in.tumcampusapp.component.tumui.roomfinder.RoomFinderActivity;
import de.tum.in.tumcampusapp.component.ui.studyroom.model.StudyRoom;
import de.tum.in.tumcampusapp.component.ui.studyroom.model.StudyRoomGroup;
import de.tum.in.tumcampusapp.utils.NetUtils;
import de.tum.in.tumcampusapp.utils.Utils;

/**
 * Shows information about reservable study rooms.
 */
public class StudyRoomsActivity extends ActivityForLoadingInBackground<Void, Void>
        implements AdapterView.OnItemSelectedListener {

    private List<StudyRoomGroup> mStudyRoomGroupList;
    private int mSelectedStudyRoomGroupId = -1;
    private ViewPager mViewPager;
    private StudyRoomsPagerAdapter mSectionsPagerAdapter;

    public StudyRoomsActivity() {
        super(R.layout.activity_study_rooms);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = findViewById(R.id.pager);
    }

    /**
     * Setup for switching study room locations via action bar
     */
    @Override
    protected void onStart() {
        super.onStart();
        startLoading();
    }

    private void selectCurrentSpinnerItem(Spinner spinner) {
        for (int i = 0; i < mStudyRoomGroupList.size(); i++) {
            StudyRoomGroup s = mStudyRoomGroupList.get(i);
            if (mSelectedStudyRoomGroupId == -1 || mSelectedStudyRoomGroupId == s.getId()) {
                mSelectedStudyRoomGroupId = s.getId();
                spinner.setSelection(i);
                return;
            }
        }
    }

    private static void sortStudyRoomsByOccupation(List<StudyRoom> studyRooms) {
        Collections.sort(studyRooms, (lhs, rhs) -> lhs.getOccupiedTill()
                                                      .compareTo(rhs.getOccupiedTill()));
    }

    private Spinner getStudyRoomGroupsSpinner() {
        // Adapter for drop-down navigation
        SpinnerAdapter adapterCafeterias =
                new ArrayAdapter<StudyRoomGroup>(this, R.layout.simple_spinner_item_actionbar,
                                                 android.R.id.text1, mStudyRoomGroupList) {
                    final LayoutInflater inflater = LayoutInflater.from(getContext());

                    @Override
                    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                        View v = inflater.inflate(R.layout.simple_spinner_dropdown_item_actionbar,
                                                  parent, false);
                        StudyRoomGroup studyRoomGroup = getItem(position);

                        TextView nameTextView = v.findViewById(android.R.id.text1);
                        TextView detailsTextView = v.findViewById(android.R.id.text2);

                        if (studyRoomGroup != null) {
                            String name = studyRoomGroup.getName();
                            String details = studyRoomGroup.getDetails();

                            nameTextView.setText(name);
                            detailsTextView.setText(details);

                            if (details.isEmpty()) {
                                detailsTextView.setVisibility(View.GONE);
                            }
                        }

                        return v;
                    }
                };

        Spinner spinner = findViewById(R.id.spinnerToolbar);
        spinner.setAdapter(adapterCafeterias);
        spinner.setOnItemSelectedListener(this);
        return spinner;
    }

    private boolean hasGotStudyRoomGroups() {
        return mStudyRoomGroupList != null && !mStudyRoomGroupList.isEmpty();
    }

    private void showCorrectErrorLayout() {
        if (NetUtils.isConnected(this)) {
            showErrorLayout();
        } else {
            showNoInternetLayout();
        }
    }

    /**
     * A new study room group has been selected -> Switch.
     *
     * @param parent the parent view
     * @param pos    index of the new selection
     * @param id     id of the selected item
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mSelectedStudyRoomGroupId = mStudyRoomGroupList.get(pos)
                                                       .getId();

        if (mSectionsPagerAdapter == null) {
            setupViewPagerAdapter(mSelectedStudyRoomGroupId);
        } else {
            changeViewPagerAdapter(mSelectedStudyRoomGroupId);
        }
    }

    private void changeViewPagerAdapter(int selectedRoomGroupId) {
        mViewPager.setAdapter(null); //unset the adapter for updating
        mSectionsPagerAdapter.setStudyRoomGroupId(this, selectedRoomGroupId);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private void setupViewPagerAdapter(int selectedRoomGroupId) {
        mSectionsPagerAdapter = new StudyRoomsPagerAdapter(getSupportFragmentManager());
        changeViewPagerAdapter(selectedRoomGroupId);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //
    }

    public void openLink(View view) {
        String link = (String) view.getTag();
        String roomCode = link.substring(link.indexOf(' ') + 1, link.length());

        Intent findStudyRoomIntent = new Intent();
        findStudyRoomIntent.putExtra(SearchManager.QUERY, roomCode);
        findStudyRoomIntent.setClass(this, RoomFinderActivity.class);
        startActivity(findStudyRoomIntent);
    }

    @Override
    protected Void onLoadInBackground(Void... arg) {
        StudyRoomGroupManager sm = new StudyRoomGroupManager(this);
        try {
            sm.downloadFromExternal();
        } catch (JSONException e) {
            Utils.log(e);
            // No error handling here
        }
        return null;
    }

    @Override
    protected void onLoadFinished(Void result) {
        StudyRoomGroupManager studyRoomGroupManager = new StudyRoomGroupManager(this);
        mStudyRoomGroupList = studyRoomGroupManager.getAllFromDb();
        for (StudyRoomGroup group : mStudyRoomGroupList) {
            sortStudyRoomsByOccupation(group.getRooms());
        }

        if (hasGotStudyRoomGroups()) {
            Spinner spinner = getStudyRoomGroupsSpinner();
            selectCurrentSpinnerItem(spinner);
        } else {
            showCorrectErrorLayout();
        }
        showLoadingEnded();
    }
}
