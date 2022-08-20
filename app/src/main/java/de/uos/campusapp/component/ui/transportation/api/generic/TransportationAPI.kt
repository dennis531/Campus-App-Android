package de.uos.campusapp.component.ui.transportation.api.generic

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.transportation.model.Departure
import de.uos.campusapp.component.ui.transportation.model.Station

interface TransportationAPI: BaseAPI {
    fun getStations(query: String, maxResults: Int = 10): List<Station>
    fun getDepartures(station: Station): List<Departure>
}