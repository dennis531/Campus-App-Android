package de.tum.`in`.tumcampusapp.component.tumui.roomfinder.api

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderCoordinateInterface
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderRoomInterface
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderScheduleInterface

interface RoomFinderAPI: BaseAPI {
    fun searchRooms(query: String): List<RoomFinderRoomInterface>
    fun fetchRoomSchedule(room: RoomFinderRoomInterface): List<RoomFinderScheduleInterface>?
    fun fetchRoomCoordinates(room: RoomFinderRoomInterface): RoomFinderCoordinateInterface?
}