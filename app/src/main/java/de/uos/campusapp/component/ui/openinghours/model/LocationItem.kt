package de.uos.campusapp.component.ui.openinghours.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

@Entity(tableName = "Location")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class LocationItem(
    @PrimaryKey
    var id: String = "",
    var category: String = "",
    var name: String = "",

    // optional properties
    var hours: String = "",
    var address: String = "",
    var room: String = "",
    var transport: String = "",
    var info: String = "",
    var url: String = ""
)
