package de.tum.`in`.tumcampusapp.component.ui.transportation.api

import android.content.Context
import com.google.gson.GsonBuilder
import de.tum.`in`.tumcampusapp.api.general.ApiHelper
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MvvClient {
    companion object {
        private const val BASE_URL = "https://efa.mvv-muenchen.de/mobile/" // VOSpilot nutzt eine API der hacon mbgh: webhost-10.hacon.de (213.83.5.140)
        private var service: MvvApiService? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): MvvApiService {
            if (service == null) {
                service = buildService(context)
            }
            return service!!
        }

        private fun buildService(context: Context): MvvApiService {
            val gson = GsonBuilder()
                    .registerTypeAdapter(DateTime::class.java, MvvDateSerializer())
                    .registerTypeAdapter(MvvStationList::class.java, MvvStationListSerializer())
                    .create()
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(ApiHelper.getOkHttpClient(context))
                    .build()
                    .create(MvvApiService::class.java)
        }
    }
}