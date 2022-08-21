package de.uos.campusapp.component.tumui.person.model

/**
 * Simple implementation of [RoomInterface]
 */
data class Room(
override var location: String,
override var number: String
) : RoomInterface {

    override fun getFullLocation(): String = "$location ($number)"

    override fun getQueryString(): String = location
}