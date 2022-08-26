package de.uos.campusapp.component.other.locations.model

import android.content.Context
import android.location.Location
import androidx.annotation.StringRes
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteria
import de.uos.campusapp.component.ui.transportation.model.AbstractStation

/**
 * Holds campus configurations
 */
class Campus {
    val id: String
    private val name: Any
    val latitude: Double
    val longitude: Double
    val cafeterias: List<AbstractCafeteria>?
    val stations: List<AbstractStation>?

    /**
    * @param id unique identifier like a short name, "X" is reserved for "no default campus"
    * @param cafeterias Used in the cafeteria component. If no cafeterias are provided the nearest cafeteria will be recommended.
    * @param stations Needed for the transportation component
    */
    constructor(
        id: String,
        name: String,
        latitude: Double,
        longitude: Double,
        cafeterias: List<AbstractCafeteria>? = null,
        stations: List<AbstractStation>? = null
    ) {
        this.id = id
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
        this.cafeterias = cafeterias
        this.stations = stations
    }

    /**
     * @param id unique identifier like a short name, "X" is reserved for "no default campus"
     * @param cafeterias Used in the cafeteria component. If no cafeterias are provided the nearest cafeteria will be recommended.
     * @param stations Needed for the transportation component
     */
    constructor(
        id: String,
        @StringRes nameResId: Int,
        latitude: Double,
        longitude: Double,
        cafeterias: List<AbstractCafeteria>? = null,
        stations: List<AbstractStation>? = null
    ) {
        this.id = id
        this.name = nameResId
        this.latitude = latitude
        this.longitude = longitude
        this.cafeterias = cafeterias
        this.stations = stations
    }

    fun getName(context: Context): String {
        if (name is Int) {
            return context.getString(name)
        }

        return name as String
    }

    fun getLocation(): Location {
        return Location("defaultLocation").apply { latitude = this@Campus.latitude; longitude = this@Campus.longitude }
    }
}