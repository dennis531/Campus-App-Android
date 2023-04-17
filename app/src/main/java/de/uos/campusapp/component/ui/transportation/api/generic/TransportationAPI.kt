package de.uos.campusapp.component.ui.transportation.api.generic

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.transportation.model.AbstractDeparture
import de.uos.campusapp.component.ui.transportation.model.AbstractStation

/**
 * Api interface for the transportation component
 */
interface TransportationAPI : BaseAPI {

    /**
     * Searches Stations
     *
     * @param query Station search string
     * @param maxResults Max number of results. This parameter could be ignored if not supported.
     * @return List of stations
     */
    fun getStations(query: String, maxResults: Int = 10): List<AbstractStation>

    /**
     * Gets departures of a given station
     *
     * @param station Station
     * @return List of station departures
     */
    fun getDepartures(station: AbstractStation): List<AbstractDeparture>
}