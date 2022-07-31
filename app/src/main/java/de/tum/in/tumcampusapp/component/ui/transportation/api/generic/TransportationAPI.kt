package de.tum.`in`.tumcampusapp.component.ui.transportation.api.generic

import de.tum.`in`.tumcampusapp.component.ui.transportation.model.Departure
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.Station

interface TransportationAPI {
    fun getStations(query: String, maxResults: Int = 10): List<Station>
    fun getDepartures(station: Station): List<Departure>
}