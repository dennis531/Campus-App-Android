package de.uos.campusapp.api.uosbackend

import android.content.Context
import com.google.gson.GsonBuilder
import de.uos.campusapp.api.general.ApiHelper
import de.uos.campusapp.api.general.DateSerializer
import de.uos.campusapp.api.general.CacheControl
import de.uos.campusapp.api.cache.interceptors.CacheResponseInterceptor
import de.uos.campusapp.api.uosbackend.serializers.cafeteria.UOSBackendCafeteriaPriceSerializer
import de.uos.campusapp.component.other.locations.LocationManager
import de.uos.campusapp.component.ui.cafeteria.api.generic.CafeteriaAPI
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteria
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteriaMenu
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenuPriceInterface
import de.uos.campusapp.component.ui.transportation.api.generic.TransportationAPI
import de.uos.campusapp.component.ui.transportation.model.AbstractDeparture
import de.uos.campusapp.component.ui.transportation.model.AbstractStation
import de.uos.campusapp.utils.CacheManager
import de.uos.campusapp.utils.ConfigConst
import de.uos.campusapp.utils.ConfigUtils
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * An OkHttpClient which connects to the Backend API. The API provides methods to get
 * cafeteria and transportation data.
 *
 * The Backend simply passes the transportation data from the OpenTripPlaner API of the VBN.
 */
class UOSBackendAPIClient(private val apiService: UOSBackendAPIService, context: Context) : CafeteriaAPI, TransportationAPI {

    val locationManager: LocationManager = LocationManager(context)

    override fun getCafeterias(): List<AbstractCafeteria> {
        return apiService.getCafeterias(CacheControl.BYPASS_CACHE.header).execute().body()!!
    }

    override fun getCafeteriaMenus(): List<AbstractCafeteriaMenu> {
        return apiService.getCafeteriaMenus(CacheControl.BYPASS_CACHE.header).execute().body()!!
    }

    /**
     * Only delivers the nearest stations within 1 km radius because the OpenTripPlaner API doesn't support search by name.
     */
    override fun getStations(query: String, maxResults: Int): List<AbstractStation> {
        val location = locationManager.getCurrentOrNextLocation()

        val stations = apiService.getStations(location.latitude, location.longitude, VBN_SEARCH_RADIUS).execute().body()!!
            .map { it.toStation() }

        // Count occurrences of query words (no multiple counting)
        val queryWords = query.split(' ')
        stations.forEach { station ->
            station.quality = queryWords.filter { station.name.contains(it, true) }.count()
        }

        return stations
    }

    /**
     * Fetches the departures of the given station from the backend.
     */
    override fun getDepartures(station: AbstractStation): List<AbstractDeparture> {
        val response = apiService.getDepartures(station.id).execute().body()!!

        return response.map { it.toDepartureList() }.flatten()
    }

    companion object {
        private val BASE_URL = ConfigUtils.getConfig(ConfigConst.UOS_BACKEND_API_BASE_URL, "")

        private const val VBN_SEARCH_RADIUS = 1000 // 1 km

        private var apiClient: UOSBackendAPIClient? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): UOSBackendAPIClient {
            if (apiClient == null) {
                apiClient = buildAPIClient(context)
            }

            return apiClient!!
        }

        private fun buildAPIClient(context: Context): UOSBackendAPIClient {
            // We cache the cafeteria menu for one day. We use Interceptors to add the appropriate
            // cache-control headers to the response.
            val cacheManager = CacheManager(context)

            val client = ApiHelper.getOkHttpClient(context)
                .newBuilder()
                .cache(cacheManager.cache)
                .addNetworkInterceptor(CacheResponseInterceptor())
                .build()

            val gson = GsonBuilder()
                .registerTypeAdapter(DateTime::class.java, DateSerializer())
                .registerTypeAdapter(CafeteriaMenuPriceInterface::class.java, UOSBackendCafeteriaPriceSerializer())
                .create()

            val apiService = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(UOSBackendAPIService::class.java)

            return UOSBackendAPIClient(apiService, context)
        }
    }
}
