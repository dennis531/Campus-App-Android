package de.uos.campusapp.api.studip.model.person

import de.uos.campusapp.component.ui.person.model.RoomInterface

class StudipRoom(
    override var location: String = "",
    override var number: String = ""
) : RoomInterface {

    override fun getFullLocation(): String = location
    override fun getQueryString(): String = location
}