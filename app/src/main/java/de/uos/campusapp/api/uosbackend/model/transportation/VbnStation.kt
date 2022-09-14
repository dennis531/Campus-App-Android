package de.uos.campusapp.api.uosbackend.model.transportation

import de.uos.campusapp.component.ui.transportation.model.AbstractStation
import de.uos.campusapp.component.ui.transportation.model.Station

data class VbnStation(
    val id: String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val dist: Int = 0
) {
    fun toStation(): AbstractStation {
        return Station(id, name)
    }
}
