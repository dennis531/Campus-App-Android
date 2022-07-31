package de.tum.`in`.tumcampusapp.component.ui.cafeteria.api.munich

import de.tum.`in`.tumcampusapp.component.ui.cafeteria.api.munich.model.MunichCafeteriaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MunichCafeteriaAPIService {

    @GET("exportDB.php?mensa_id=all")
    fun getMenus(
        @Header("Cache-Control") cacheControl: String
    ): Call<MunichCafeteriaResponse>
}