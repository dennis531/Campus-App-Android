package de.uos.campusapp.component.ui.openinghours;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.uos.campusapp.R;
import de.uos.campusapp.component.other.generic.adapter.EqualSpacingItemDecoration;
import de.uos.campusapp.component.ui.openinghours.model.LocationItem;
import de.uos.campusapp.database.TcaDb;

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a {@link OpeningHoursListActivity} in two-pane mode (on tablets)
 * or a {@link OpeningHoursDetailActivity} on handsets.
 * <p/>
 * NEEDS: ARG_ITEM_ID and ARG_ITEM_CONTENT set in arguments
 */
public class OpeningHoursDetailFragment extends Fragment {
    static final String ARG_ITEM_CATEGORY = "item_category";
    static final String TWO_PANE = "two_pane";

    private String mItemCategory;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OpeningHoursDetailFragment() {
        // NOP
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_CATEGORY)) {
            mItemCategory = getArguments().getString(ARG_ITEM_CATEGORY);
        }
        if (!getArguments().containsKey(TWO_PANE) || !getArguments().getBoolean(TWO_PANE)) {
            getActivity().setTitle(mItemCategory);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        // load all locations from category
        LocationDao dao = TcaDb.Companion.getInstance(getActivity())
                                         .locationDao();
        List<LocationItem> locations = dao.getAllOfCategory(mItemCategory);

        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_item_detail_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new OpeningHoursDetailAdapter(locations));

        int spacing = Math.round(getResources().getDimension(R.dimen.material_card_view_padding));
        recyclerView.addItemDecoration(new EqualSpacingItemDecoration(spacing));

        return rootView;
    }

    /**
     * change presentation of locations in the list
     */
    public static void bindLocationToView(View view, LocationItem location) {
        TextView locationTextView = view.findViewById(R.id.headerTextView);
        locationTextView.setText(location.getName());

        String hours = location.getHours();
        String address = location.getAddress();
        String room = location.getRoom();
        String transport = location.getTransport();
        String info = location.getInfo();

        MaterialButton openLinkButton = view.findViewById(R.id.openLinkButton);
        if (location.getUrl()
                    .isEmpty()) {
            openLinkButton.setVisibility(View.GONE);
        } else {
            openLinkButton.setVisibility(View.VISIBLE);
            openLinkButton.setText(R.string.website);
            openLinkButton.setTag(location.getUrl());
        }

        TextView hoursView = view.findViewById(R.id.opening_hours_hours);
        hoursView.setVisibility(hours.isEmpty() ? View.GONE : View.VISIBLE);
        hoursView.setText(hours);

        TextView locationView = view.findViewById(R.id.opening_hours_location);
        locationView.setVisibility(address.isEmpty() ? View.GONE : View.VISIBLE);
        locationView.setText(address);

        TextView roomView = view.findViewById(R.id.opening_hours_room);
        roomView.setVisibility(room.isEmpty() ? View.GONE : View.VISIBLE);
        roomView.setText(room);

        TextView transportView = view.findViewById(R.id.opening_hours_transport);
        transportView.setVisibility(transport.isEmpty() ? View.GONE : View.VISIBLE);
        transportView.setText(transport);

        TextView infoView = view.findViewById(R.id.opening_hours_info);
        infoView.setVisibility(info.isEmpty() ? View.GONE : View.VISIBLE);
        infoView.setText(info);

        // link email addresses and phone numbers (e.g. 089-123456)
        Linkify.addLinks(infoView, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);
        Linkify.addLinks(infoView, Pattern.compile("[0-9-]+"), "tel:");
    }

    private class OpeningHoursDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final List<LocationItem> locations;

        OpeningHoursDetailAdapter(List<LocationItem> locations) {
            this.locations = locations;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                                   .inflate(R.layout.card_opening_hour_details, parent, false);
            return new RecyclerView.ViewHolder(v) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OpeningHoursDetailFragment.bindLocationToView(holder.itemView, locations.get(position));
        }

        @Override
        public int getItemCount() {
            return locations.size();
        }

    }

}
