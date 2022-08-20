package de.uos.campusapp.component.ui.overview

import android.os.Bundle
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.Utils

/**
 * Main activity displaying the cards and providing navigation with navigation drawer
 */
class MainActivity : BaseActivity(R.layout.activity_main, Component.OVERVIEW) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.log("MainActivity created")

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, MainFragment.newInstance())
                    .commit()
        }
    }
}
