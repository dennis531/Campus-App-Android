package de.uos.campusapp.component.ui.transportation.api.vbn

import android.content.Context
import com.google.gson.GsonBuilder
import de.uos.campusapp.api.general.ApiHelper
import de.uos.campusapp.component.other.locations.LocationManager
import de.uos.campusapp.component.ui.transportation.api.generic.TransportationAPI
import de.uos.campusapp.component.ui.transportation.model.AbstractDeparture
import de.uos.campusapp.component.ui.transportation.model.AbstractStation
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class VbnClient(val service: VbnApiService, context: Context): TransportationAPI {

    val locationManager: LocationManager = LocationManager(context)

    /**
     * Only delivers the nearest stations because the OpenTripPlaner API doesn't support search by name.
     */
    override fun getStations(query: String, maxResults: Int): List<AbstractStation> {
        //
        val location = locationManager.getCurrentOrNextLocation()

        val stations = service.getStations(location.latitude, location.longitude).execute().body()!!
            .map { it.toStation() }

        // Count occurrences of query words (no multiple counting)
        val queryWords = query.split(' ')
        stations.forEach { station ->
            station.quality = queryWords.filter { station.name.contains(it, true) }.count()
        }

        return stations
    }

    override fun getDepartures(station: AbstractStation): List<AbstractDeparture> {
        val response = service.getDepartures(station.id).execute().body()!!

        return response.map { it.toDepartureList() }.flatten()
    }

    companion object {
        private const val BASE_URL = "http://gtfsr.vbn.de/api/"
        private var client: VbnClient? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): VbnClient {
            if (client == null) {
                client = buildClient(context)
            }
            return client!!
        }

        private fun buildClient(context: Context): VbnClient {
            val gson = GsonBuilder().create()
            val service = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(ApiHelper.getOkHttpClient(context))
                    .build()
                    .create(VbnApiService::class.java)

            return VbnClient(service, context)
        }
    }
}