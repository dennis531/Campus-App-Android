package de.uos.campusapp.component.ui.roomfinder.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.roomfinder.model.RoomFinderCoordinateInterface
import de.uos.campusapp.component.ui.roomfinder.model.RoomFinderRoomInterface
import de.uos.campusapp.component.ui.roomfinder.model.RoomFinderScheduleInterface

/**
 * Api interface for the roomfinder component
 */
interface RoomFinderAPI : BaseAPI {

    /**
     * Searches a room in the external system
     *
     * @param query Room search string
     * @return List of found rooms
     */
    fun searchRooms(query: String): List<RoomFinderRoomInterface>

    /**
     * Fetches the schedule of a room
     *
     * @param room Room from which the schedule is requested
     * @return List of room schedule entries
     */
    fun fetchRoomSchedule(room: RoomFinderRoomInterface): List<RoomFinderScheduleInterface>?

    /**
     * Fetches the coordinates of a room
     *
     * @param room Room
     * @return Room coordinates
     */
    fun fetchRoomCoordinates(room: RoomFinderRoomInterface): RoomFinderCoordinateInterface?
}