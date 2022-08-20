package de.uos.campusapp.component.tumui.person.model

import java.io.Serializable

interface RoomInterface: Serializable {
    var location: String
    var number: String

    fun getFullLocation(): String
    fun getQueryString(): String

    companion object {
        private const val serialVersionUID = -1577861629845776668L
    }
}