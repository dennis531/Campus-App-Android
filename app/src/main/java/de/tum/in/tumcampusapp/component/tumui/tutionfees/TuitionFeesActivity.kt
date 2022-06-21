package de.tum.`in`.tumcampusapp.component.tumui.tutionfees

import android.os.Bundle
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.activity.BaseActivity
import de.tum.`in`.tumcampusapp.utils.Component

/**
 * Activity to show the user's tuition fees status
 */
class TuitionFeesActivity : BaseActivity(R.layout.activity_tuitionfees, Component.TUTIONFEES) {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, TuitionFeesFragment.newInstance())
                    .commit()
        }
    }
}
