package de.uos.campusapp.component.ui.tuitionfees

import android.os.Bundle
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.utils.Component

/**
 * Activity to show the user's tuition fees status
 */
class TuitionFeesActivity : BaseActivity(R.layout.activity_tuitionfees, Component.TUITIONFEES) {

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
