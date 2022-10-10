package de.uos.campusapp.component.ui.calendar

import android.content.Context
import androidx.core.content.ContextCompat
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.calendar.model.CalendarItem
import de.uos.campusapp.component.ui.calendar.model.CalendarEventType
import de.uos.campusapp.utils.ColorUtils.getDisplayColorFromColor

class EventColorProvider(private val context: Context) {

    fun getColor(calendarItem: CalendarItem): Int {
        val colorResId = when (calendarItem.type) {
            CalendarEventType.LECTURE -> R.color.event_lecture
            CalendarEventType.EXERCISE -> R.color.event_exercise
            CalendarEventType.CANCELED -> R.color.event_canceled
            CalendarEventType.OTHER -> R.color.event_other
        }
        return getDisplayColorFromColor(ContextCompat.getColor(context, colorResId))
    }
}
