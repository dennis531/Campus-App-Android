package de.uos.campusapp.component.ui.transportation.api.mvv

import android.content.Context
import com.google.gson.GsonBuilder
import de.uos.campusapp.api.general.ApiHelper
import de.uos.campusapp.component.ui.transportation.api.generic.TransportationAPI
import de.uos.campusapp.component.ui.transportation.api.mvv.model.MvvStationList
import de.uos.campusapp.component.ui.transportation.model.AbstractDeparture
import de.uos.campusapp.component.ui.transportation.model.AbstractStation
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MvvClient(val service: MvvApiService) : TransportationAPI {

    override fun getStations(query: String, maxResults: Int): List<AbstractStation> {
        val mvvStationList = service.getStations(query).execute().body()!!
        return mvvStationList.stations
    }

    override fun getDepartures(station: AbstractStation): List<AbstractDeparture> {
        val mvvDepartureList = service.getDepartures(station.id).execute().body()!!
        return mvvDepartureList.departureList?.map { it.toDeparture() } ?: emptyList()
    }

    companion object {
        private const val BASE_URL = "https://efa.mvv-muenchen.de/mobile/"
        private var client: MvvClient? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): MvvClient {
            if (client == null) {
                client = buildClient(context)
            }
            return client!!
        }

        private fun buildClient(context: Context): MvvClient {
            val gson = GsonBuilder()
                    .registerTypeAdapter(DateTime::class.java, MvvDateSerializer())
                    .registerTypeAdapter(MvvStationList::class.java, MvvStationListSerializer())
                    .create()
            val service = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(ApiHelper.getOkHttpClient(context))
                    .build()
                    .create(MvvApiService::class.java)

            return MvvClient(service)
        }
    }
}