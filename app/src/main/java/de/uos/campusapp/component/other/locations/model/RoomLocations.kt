package de.uos.campusapp.component.other.locations.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

/**
 * Entity for lecture room locations
 */
@Entity(tableName = "room_locations")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class RoomLocations(
    @PrimaryKey
    var title: String = "",
    var latitude: String = "",
    var longitude: String = ""
) {

    constructor(title: String, geo: Geo) : this(title, geo.latitude, geo.longitude)

    /**
     * Retrieve Geo object with related information plugged
     */
    fun toGeo(): Geo {
        return Geo(latitude, longitude)
    }
}
