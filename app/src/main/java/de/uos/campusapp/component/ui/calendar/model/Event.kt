package de.uos.campusapp.component.ui.calendar.model

import org.joda.time.DateTime

/**
 * Simple implementation of [AbstractEvent]
 */
class Event(
    override val id: String?,
    override val title: String,
    override val type: CalendarItemType?,
    override val description: String?,
    override val dtstart: DateTime,
    override val dtend: DateTime,
    override val location: String?,
    override val isEditable: Boolean?
) : AbstractEvent()