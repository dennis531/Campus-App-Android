package de.tum.`in`.tumcampusapp.api.studip.model.person

import de.tum.`in`.tumcampusapp.component.tumui.person.model.RoomInterface

class StudipRoom(
    override var location: String = "",
    override var number: String = ""
) : RoomInterface {

    override fun getFullLocation(): String = location
    override fun getQueryString(): String = location
}