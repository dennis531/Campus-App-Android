package de.uos.campusapp.component.ui.cafeteria.api.munich

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.uos.campusapp.api.general.ApiHelper
import de.uos.campusapp.api.general.DateSerializer
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.api.tumonline.interceptors.CacheResponseInterceptor
import de.uos.campusapp.component.ui.cafeteria.api.generic.CafeteriaAPI
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteriaMenu
import de.uos.campusapp.component.ui.cafeteria.model.Cafeteria
import de.uos.campusapp.utils.CacheManager
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MunichCafeteriaAPIClient(private val apiService: MunichCafeteriaAPIService): CafeteriaAPI {

    override fun getCafeterias(): List<Cafeteria> {
        // TODO: Remove hardcoded response
        val json = "[{\"mensa\":\"5\",\"id\":\"421\",\"name\":\"Mensa Arcisstra\\u00dfe\",\"address\":\"Arcisstr. 17, M\\u00fcnchen\",\"latitude\":\"48.147312\",\"longitude\":\"11.567229\"},{\"mensa\":\"6\",\"id\":\"422\",\"name\":\"Mensa Garching\",\"address\":\"Lichtenbergstr. 2, Garching\",\"latitude\":\"48.267509\",\"longitude\":\"11.671278\"},{\"mensa\":\"1\",\"id\":\"411\",\"name\":\"Mensa Leopoldstra\\u00dfe\",\"address\":\"Leopoldstra\\u00dfe 13a, M\\u00fcnchen\",\"latitude\":\"48.156586\",\"longitude\":\"11.582004\"},{\"mensa\":\"8\",\"id\":\"431\",\"name\":\"Mensa Lothstra\\u00dfe\",\"address\":\"Lothstr. 13 d, M\\u00fcnchen\",\"latitude\":\"48.154003\",\"longitude\":\"11.552526\"},{\"mensa\":\"2\",\"id\":\"412\",\"name\":\"Mensa Martinsried\",\"address\":\"Gro\\u00dfhaderner Stra\\u00dfe 6, Planegg-Martinsried\",\"latitude\":\"48.109894\",\"longitude\":\"11.459931\"},{\"mensa\":\"9\",\"id\":\"432\",\"name\":\"Mensa Pasing\",\"address\":\"Am Stadtpark 20, M\\u00fcnchen\",\"latitude\":\"48.141586\",\"longitude\":\"11.450717\"},{\"mensa\":\"7\",\"id\":\"423\",\"name\":\"Mensa Weihenstephan\",\"address\":\"Maximus-von-Imhof-Forum 5, Freising\",\"latitude\":\"48.399590\",\"longitude\":\"11.723350\"},{\"mensa\":\"3\",\"id\":\"414\",\"name\":\"Mensaria Gro\\u00dfhadern\",\"address\":\"Butenandtstr. 13 Geb\\u00e4ude F, M\\u00fcnchen\",\"latitude\":\"48.113762\",\"longitude\":\"11.467660\"},{\"mensa\":\"10\",\"id\":\"441\",\"name\":\"StuBistro Mensa Rosenheim\",\"address\":\"Hochschulstr. 1, Rosenheim\",\"latitude\":\"47.867451\",\"longitude\":\"12.106990\"},{\"mensa\":\"4\",\"id\":\"416\",\"name\":\"StuBistro Schellingstra\\u00dfe\",\"address\":\"Schellingstr. 3, M\\u00fcnchen\",\"latitude\":\"48.149300\",\"longitude\":\"11.579093\"},{\"mensa\":\"11\",\"id\":\"512\",\"name\":\"StuCaf\\u00e9 Adalbertstra\\u00dfe\",\"address\":\"Adalbertstr. 5, M\\u00fcnchen\",\"latitude\":\"48.151428\",\"longitude\":\"11.580292\"},{\"mensa\":\"14\",\"id\":\"526\",\"name\":\"StuCaf\\u00e9 Akademie\",\"address\":\"Alte Akademie 1, Freising\",\"latitude\":\"48.395134\",\"longitude\":\"11.728629\"},{\"mensa\":\"15\",\"id\":\"527\",\"name\":\"StuCaf\\u00e9 Boltzmannstra\\u00dfe\",\"address\":\"Boltzmannstr. 15, Garching\",\"latitude\":\"48.265842\",\"longitude\":\"11.667780\"},{\"mensa\":\"16\",\"id\":\"532\",\"name\":\"StuCaf\\u00e9 Karlstra\\u00dfe\",\"address\":\"Karlstr. 6, M\\u00fcnchen\",\"latitude\":\"48.142761\",\"longitude\":\"11.568387\"},{\"mensa\":\"12\",\"id\":\"524\",\"name\":\"StuCaf\\u00e9 Mensa Garching\",\"address\":\"Lichtenbergstr. 2, Garching\",\"latitude\":\"48.267509\",\"longitude\":\"11.671278\"},{\"mensa\":\"13\",\"id\":\"525\",\"name\":\"StuCaf\\u00e9 Mensa-WST\",\"address\":\"Maximus-von-Imhof-Forum 5, Freising\",\"latitude\":\"48.398453\",\"longitude\":\"11.724441\"}]"

        return Gson().fromJson(json, Array<Cafeteria>::class.java).toList()
    }

    override fun getMenus(): List<AbstractCafeteriaMenu> {
        val response = apiService.getMenus(CacheControl.BYPASS_CACHE.header).execute().body()!!

        val menus = response.menus + response.sideDishes

        return menus.map { it.toCafeteriaMenu() }
    }

    companion object {

        private const val BASE_URL = "https://www.devapp.it.tum.de/mensaapp/"

        private var apiClient: MunichCafeteriaAPIClient? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): MunichCafeteriaAPIClient {
            if (apiClient == null) {
                apiClient = buildAPIClient(context)
            }

            return apiClient!!
        }

        private fun buildAPIClient(context: Context): MunichCafeteriaAPIClient {
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
                    .create()

            val apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(MunichCafeteriaAPIService::class.java)

            return MunichCafeteriaAPIClient(apiService)
        }
    }
}