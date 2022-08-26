package de.uos.campusapp.component.ui.transportation.api.generic

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.transportation.model.AbstractDeparture
import de.uos.campusapp.component.ui.transportation.model.AbstractStation

interface TransportationAPI: BaseAPI {
    fun getStations(query: String, maxResults: Int = 10): List<AbstractStation>
    fun getDepartures(station: AbstractStation): List<AbstractDeparture>
}