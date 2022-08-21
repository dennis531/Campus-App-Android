package de.uos.campusapp.component.tumui.roomfinder.model

import org.joda.time.DateTime

/**
 * Represents a room reservation in the schedule
 *
 * @property id
 * @property title name of reservation
 * @property start reservation start
 * @property end reservation end
 */
interface RoomFinderScheduleInterface {
    val id: String
    val title: String
    val start: DateTime
    val end: DateTime
}
