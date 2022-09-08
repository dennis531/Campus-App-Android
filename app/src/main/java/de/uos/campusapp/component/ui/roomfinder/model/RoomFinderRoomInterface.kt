package de.uos.campusapp.component.ui.roomfinder.model

import de.uos.campusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter
import java.io.Serializable

/**
 * Represents a room finder room
 *
 * @property id
 * @property name room name, e.g. "12/E01"
 * @property address room/building address, e.g. "Seminarstr. 1" (optional)
 * @property campus Campus Name, e.g. "Innenstadt" (optional)
 * @property info Information text (optional)
 * @property imageUrl Url containing a map or a picture of the related building or room (optional)
 */
interface RoomFinderRoomInterface: SimpleStickyListHeadersAdapter.SimpleStickyListItem, Serializable {
    val id: String
    val name: String
    val address: String?
    val campus: String?
    val info: String?
    val imageUrl: String? // Image with map or other

    override fun getHeadName() = this.name

    override fun getHeaderId() = getHeadName()

    companion object {
        private const val serialVersionUID = 6631656320611471476L
    }
}