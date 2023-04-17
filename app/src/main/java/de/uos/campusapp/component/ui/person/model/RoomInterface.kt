package de.uos.campusapp.component.ui.person.model

import java.io.Serializable

/**
 * Represents a room
 *
 * @property location address of room or related building
 * @property number Room number
 */
interface RoomInterface : Serializable {
    var location: String
    var number: String

    /**
     * Get full formatted location for room
     */
    fun getFullLocation(): String

    /**
     * Return identifier string for room search in the roomfinder component
     *
     * Simply return empty string if roomfinder component is disabled or search should not be available.
     */
    fun getQueryString(): String

    companion object {
        private const val serialVersionUID = -1577861629845776668L
    }
}