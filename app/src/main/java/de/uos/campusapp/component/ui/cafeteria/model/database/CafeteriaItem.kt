package de.uos.campusapp.component.ui.cafeteria.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

/**
 * Database Cafeteria Item
 *
 * @param id Cafeteria ID, e.g. 412
 * @param name Name, e.g. MensaX
 * @param address Address, e.g. Boltzmannstr. 3 (optional)
 * @param latitude Coordinates of the cafeteria (optional)
 * @param longitude Coordinates of the cafeteria (optional)
 */
@Entity(tableName = "cafeteria")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
open class CafeteriaItem(
    @field:PrimaryKey
    open var id: String,
    open var name: String,
    open var address: String? = null,
    open var latitude: Double? = null,
    open var longitude: Double? = null
) : Comparable<CafeteriaItem> {

    // Used for ordering cafeterias
    var distance: Float? = null

    override fun toString(): String = name

    override fun compareTo(other: CafeteriaItem): Int {
        if (distance == null || other.distance == null) {
            return name.compareTo(other.name)
        }

        return java.lang.Float.compare(distance!!, other.distance!!)
    }
}