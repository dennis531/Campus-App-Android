package de.uos.campusapp.component.tumui.roomfinder.model

import org.joda.time.DateTime

/**
 * Simple implementation of [RoomFinderScheduleInterface]
 */
data class RoomFinderSchedule(
    override val id: String,
    override val title: String,
    override val start: DateTime,
    override val end: DateTime
) : RoomFinderScheduleInterface