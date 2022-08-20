package de.uos.campusapp.component.tumui.roomfinder

import android.os.Bundle
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.utils.Component

class RoomFinderActivity : BaseActivity(R.layout.activity_roomfinder, Component.ROOMFINDER) {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, RoomFinderFragment.newInstance())
                .commit()
        }
    }
}
