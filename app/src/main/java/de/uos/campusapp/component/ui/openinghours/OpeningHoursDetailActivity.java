package de.uos.campusapp.component.ui.openinghours;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import de.uos.campusapp.R;
import de.uos.campusapp.component.other.generic.activity.BaseActivity;
import de.uos.campusapp.utils.Component;

/**
 * An activity representing a single Item detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link OpeningHoursListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link OpeningHoursDetailFragment}.
 */
public class OpeningHoursDetailActivity extends BaseActivity {

    public OpeningHoursDetailActivity() {
        super(R.layout.activity_openinghoursdetails, Component.OPENINGHOUR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(OpeningHoursDetailFragment.ARG_ITEM_CATEGORY,
                             getIntent().getStringExtra(OpeningHoursDetailFragment.ARG_ITEM_CATEGORY));

            arguments.putBoolean(OpeningHoursDetailFragment.TWO_PANE,
                                 getIntent().getBooleanExtra(OpeningHoursDetailFragment.TWO_PANE, false));

            OpeningHoursDetailFragment fragment = new OpeningHoursDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.item_detail_container, fragment)
                                       .commit();
        }
    }

    public void openLink(View view) {
        String url = (String) view.getTag();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}
