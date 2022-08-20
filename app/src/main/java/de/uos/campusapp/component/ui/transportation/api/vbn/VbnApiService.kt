package de.uos.campusapp.component.ui.transportation.api.vbn

import de.uos.campusapp.component.ui.transportation.api.vbn.model.VbnResponse
import de.uos.campusapp.component.ui.transportation.api.vbn.model.VbnStation
import retrofit2.Call
import retrofit2.http.*

/**
 * API Service for the OpenTripPlaner of the VBN
 */
interface VbnApiService {

    /**
     * Get all stations within 1 km of a coordinate
     */
    @GET("routers/connect/index/stops?radius=1000")
    fun getStations(@Query("lat") latitude: Double, @Query("lon") longitude: Double): Call<List<VbnStation>>

    /**
     * Get all departures for a station.
     * @param stationId Station ID, station name might or might not work
     */
    @GET("routers/connect/index/stops/{stopId}/stoptimes")
    fun getDepartures(
        @Path("stopId") stationId: String,
    ): Call<List<VbnResponse>>

}