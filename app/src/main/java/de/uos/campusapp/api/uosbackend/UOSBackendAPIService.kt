package de.uos.campusapp.api.uosbackend

import de.uos.campusapp.api.uosbackend.model.cafeteria.UOSBackendCafeteria
import de.uos.campusapp.api.uosbackend.model.cafeteria.UOSBackendCafeteriaMenu
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface UOSBackendAPIService {

    @GET("cafeterias/")
    fun getCafeterias(
        @Header("Cache-Control") cacheControl: String
    ): Call<List<UOSBackendCafeteria>>

    @GET("cafeteria-menus/")
    fun getCafeteriaMenus(
        @Header("Cache-Control") cacheControl: String
    ): Call<List<UOSBackendCafeteriaMenu>>
}