package de.uos.campusapp.component.ui.cafeteria.activity

import android.os.Bundle

import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.component.ui.cafeteria.fragment.CafeteriaFragment
import de.uos.campusapp.utils.Component

class CafeteriaActivity : BaseActivity(R.layout.activity_cafeteria, Component.CAFETERIA) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, CafeteriaFragment.newInstance())
                    .commit()
        }
    }
}
