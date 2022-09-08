package de.uos.campusapp.component.ui.calendar

import android.content.Context
import androidx.core.content.ContextCompat
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.calendar.model.CalendarItem
import de.uos.campusapp.component.ui.calendar.model.CalendarItemType
import de.uos.campusapp.utils.ColorUtils.getDisplayColorFromColor

class EventColorProvider(private val context: Context) {

    fun getColor(calendarItem: CalendarItem): Int {
        val colorResId = when (calendarItem.type) {
            CalendarItemType.LECTURE -> R.color.event_lecture
            CalendarItemType.EXERCISE -> R.color.event_exercise
            CalendarItemType.CANCELED -> R.color.event_canceled
            CalendarItemType.OTHER -> R.color.event_other
        }
        return getDisplayColorFromColor(ContextCompat.getColor(context, colorResId))
    }
}
