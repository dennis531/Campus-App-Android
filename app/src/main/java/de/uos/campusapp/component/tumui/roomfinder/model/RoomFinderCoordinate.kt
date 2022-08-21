package de.uos.campusapp.component.tumui.roomfinder.model

/**
 * Simple implementation of [RoomFinderCoordinateInterface]
 */
data class RoomFinderCoordinate(
    override var latitude: String,
    override var longitude: String
) : RoomFinderCoordinateInterface