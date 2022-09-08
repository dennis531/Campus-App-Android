package de.uos.campusapp.component.ui.calendar.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.calendar.model.CalendarItem
import de.uos.campusapp.component.ui.calendar.model.AbstractEvent

interface CalendarAPI: BaseAPI {
    fun getCalendar(): List<AbstractEvent>?
    fun createCalendarEvent(calendarItem: CalendarItem): String // Returns id of created event
    fun deleteCalenderEvent(id: String)
}