package de.uos.campusapp.component.ui.calendar

import android.os.Bundle
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.Const

/**
 * Activity showing the user's calendar. Calendar items (events) are fetched from LMS and displayed as blocks on a timeline.
 */
class CalendarActivity : BaseActivity(R.layout.activity_calendar, Component.CALENDAR) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val showDate = intent.getLongExtra(Const.EVENT_TIME, -1)
            val eventId = intent.getStringExtra(Const.KEY_EVENT_ID)
            val fragment = CalendarFragment.newInstance(showDate, eventId)

            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, fragment)
                    .commit()
        }
    }
}
