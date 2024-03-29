package de.uos.campusapp.component.ui.openinghours;

import android.os.Bundle;

import de.uos.campusapp.R;
import de.uos.campusapp.component.other.generic.activity.BaseActivity;
import de.uos.campusapp.utils.Component;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link OpeningHoursDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link OpeningHoursListFragment} and the item details (if present) is a
 * {@link OpeningHoursDetailFragment}.
 */
public class OpeningHoursListActivity extends BaseActivity {

    public OpeningHoursListActivity() {
        super(R.layout.activity_openinghourslist, Component.OPENINGHOUR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentFrame, OpeningHoursListFragment.newInstance())
                    .commit();
        }
    }

}
