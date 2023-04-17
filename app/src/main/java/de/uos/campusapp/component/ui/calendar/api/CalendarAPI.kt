package de.uos.campusapp.component.ui.calendar.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.calendar.model.AbstractEvent
import de.uos.campusapp.utils.ConfigConst

/**
 * Api interface for the calendar component
 */
interface CalendarAPI : BaseAPI {

    /**
     * Gets all calendar events from client
     *
     * @return List of events
     */
    fun getCalendar(): List<AbstractEvent>?

    /**
     * Creates a calender event in the external system
     *
     * Only required if config option [ConfigConst.CALENDAR_EDITABLE] is set to [true] in config.
     * Otherwise simply return an empty [String].
     *
     * @param calendarEvent new calendar event
     * @return External id of created event
     */
    fun createCalendarEvent(calendarEvent: AbstractEvent): String // Returns id of created event

    /**
     * Deletes a calender event in the external system
     *
     * Only required if config option [ConfigConst.CALENDAR_EDITABLE] is set to [true] in config.
     * Otherwise provide an empty implementation.
     *
     * @param id Id of the event to be deleted
     */
    fun deleteCalenderEvent(id: String)
}