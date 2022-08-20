package de.uos.campusapp.component.tumui.roomfinder.model

import de.uos.campusapp.component.other.general.model.Recent

/**
 * This class is used as a model for rooms in Roomfinder retrofit request.
 * @param name This is the campus name
 */
data class RoomFinderRoom(
    override val id: String = "",
    override val buildingId: String = "",
    override val name: String = "",
    override val address: String? = "",
    override val campus: String? = "",
    override val info: String? = "",
    override val imageUrl: String? = ""
): RoomFinderRoomInterface {

    companion object {
        fun fromRecent(r: Recent): RoomFinderRoom {
            val values: Array<String?> = r.name.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            if (values.size != 7) {
                throw IllegalArgumentException()
            }

            // convert null strings to null
            for (i in 3 until values.size) {
                if (values[i] == "null") {
                    values[i] = null
                }
            }

            return RoomFinderRoom(values[0]!!, values[1]!!, values[2]!!, values[3], values[4], values[5])
        }
    }
}
