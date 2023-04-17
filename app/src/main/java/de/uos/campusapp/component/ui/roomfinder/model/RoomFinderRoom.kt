package de.uos.campusapp.component.ui.roomfinder.model

import de.uos.campusapp.component.other.general.model.Recent

/**
 * This class is used as a model for rooms in Roomfinder retrofit request.
 * @param name This is the campus name
 */
data class RoomFinderRoom(
    override val id: String,
    override val name: String,
    override val address: String? = null,
    override val campus: String? = null,
    override val info: String? = null,
    override val imageUrl: String? = null
) : RoomFinderRoomInterface {

    companion object {
        fun fromRecent(r: Recent): RoomFinderRoom {
            val values: Array<String?> = r.name.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            if (values.size != 6) {
                throw IllegalArgumentException()
            }

            // convert null strings to null
            for (i in 2 until values.size) {
                if (values[i] == "null") {
                    values[i] = null
                }
            }

            return RoomFinderRoom(values[0]!!, values[1]!!, values[2], values[3], values[4], values[5])
        }
    }
}
