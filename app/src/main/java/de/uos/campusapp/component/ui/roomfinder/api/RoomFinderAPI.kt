package de.uos.campusapp.component.ui.roomfinder.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.roomfinder.model.RoomFinderCoordinateInterface
import de.uos.campusapp.component.ui.roomfinder.model.RoomFinderRoomInterface
import de.uos.campusapp.component.ui.roomfinder.model.RoomFinderScheduleInterface

interface RoomFinderAPI: BaseAPI {
    fun searchRooms(query: String): List<RoomFinderRoomInterface>
    fun fetchRoomSchedule(room: RoomFinderRoomInterface): List<RoomFinderScheduleInterface>?
    fun fetchRoomCoordinates(room: RoomFinderRoomInterface): RoomFinderCoordinateInterface?
}