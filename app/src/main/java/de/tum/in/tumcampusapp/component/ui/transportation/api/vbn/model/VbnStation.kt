package de.tum.`in`.tumcampusapp.component.ui.transportation.api.vbn.model

import de.tum.`in`.tumcampusapp.component.ui.transportation.model.Station

data class VbnStation(
    val id: String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val dist: Int = 0
) {
    fun toStation(): Station {
        return Station(id, name)
    }
}
