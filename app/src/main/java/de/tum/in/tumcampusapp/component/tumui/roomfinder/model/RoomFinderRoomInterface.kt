package de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model

import de.tum.`in`.tumcampusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter
import java.io.Serializable

interface RoomFinderRoomInterface: SimpleStickyListHeadersAdapter.SimpleStickyListItem, Serializable {
    val id: String
    val buildingId: String
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