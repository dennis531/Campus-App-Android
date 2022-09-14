package de.uos.campusapp.api.uosbackend

import de.uos.campusapp.api.uosbackend.model.cafeteria.UOSBackendCafeteria
import de.uos.campusapp.api.uosbackend.model.cafeteria.UOSBackendCafeteriaMenu
import de.uos.campusapp.api.uosbackend.model.transportation.VbnResponse
import de.uos.campusapp.api.uosbackend.model.transportation.VbnStation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface UOSBackendAPIService {

    @GET("cafeterias/")
    fun getCafeterias(
        @Header("Cache-Control") cacheControl: String
    ): Call<List<UOSBackendCafeteria>>

    @GET("cafeteria-menus/")
    fun getCafeteriaMenus(
        @Header("Cache-Control") cacheControl: String
    ): Call<List<UOSBackendCafeteriaMenu>>

    /**
     * Get all stations within the specified radius around a coordinate.
     *
     * The backend simply passes the data from the OpenTripPlaner of the VBN.
     *
     * @param latitude Latitude of the coordinate
     * @param longitude Longitude of the coordinate
     * @param radius Search radius in meter
     */
    @GET("stations/")
    fun getStations(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Int
    ): Call<List<VbnStation>>

    /**
     * Get all departures for a station.
     *
     * The backend simply passes the data from the OpenTripPlaner of the VBN.
     *
     * @param stationId Station ID
     */
    @GET("stations/{stationId}/departures")
    fun getDepartures(@Path("stationId") stationId: String): Call<List<VbnResponse>>
}