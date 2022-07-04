package de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model

import org.joda.time.DateTime

interface RoomFinderScheduleInterface {
    val id: String
    val title: String
    val start: DateTime
    val end: DateTime
}
