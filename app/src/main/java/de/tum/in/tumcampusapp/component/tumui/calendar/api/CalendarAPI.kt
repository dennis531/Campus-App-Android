package de.tum.`in`.tumcampusapp.component.tumui.calendar.api

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.component.tumui.calendar.model.CalendarItem
import de.tum.`in`.tumcampusapp.component.tumui.calendar.model.AbstractEvent

interface CalendarAPI: BaseAPI {
    fun getCalendar(): List<AbstractEvent>?
    fun createCalendarEvent(calendarItem: CalendarItem): String // Returns id of created event
    fun deleteCalenderEvent(id: String)
}