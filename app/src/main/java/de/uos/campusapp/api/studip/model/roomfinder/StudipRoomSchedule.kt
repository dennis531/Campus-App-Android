package de.uos.campusapp.api.studip.model.roomfinder

import de.uos.campusapp.component.tumui.roomfinder.model.RoomFinderScheduleInterface
import org.joda.time.DateTime

class StudipRoomSchedule(
    override val id: String,
    override val title: String,
    override val start: DateTime,
    override val end: DateTime
    ) : RoomFinderScheduleInterface