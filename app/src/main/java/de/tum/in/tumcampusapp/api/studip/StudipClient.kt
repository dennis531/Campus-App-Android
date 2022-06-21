package de.tum.`in`.tumcampusapp.api.studip

import android.content.Context
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import de.tum.`in`.tumcampusapp.api.general.ApiHelper
import de.tum.`in`.tumcampusapp.api.general.exception.UnauthorizedException
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.api.studip.interceptors.CheckErrorInterceptor
import de.tum.`in`.tumcampusapp.api.studip.model.calendar.StudipBaseEvent
import de.tum.`in`.tumcampusapp.api.studip.model.calendar.StudipCalendarEvent
import de.tum.`in`.tumcampusapp.api.studip.model.calendar.StudipCourseEvent
import de.tum.`in`.tumcampusapp.api.studip.model.lectures.StudipLecture
import de.tum.`in`.tumcampusapp.api.studip.model.lectures.StudipLectureAppointment
import de.tum.`in`.tumcampusapp.api.studip.model.person.StudipInstitute
import de.tum.`in`.tumcampusapp.api.studip.model.person.StudipPerson
import de.tum.`in`.tumcampusapp.component.tumui.calendar.api.CalendarAPI
import de.tum.`in`.tumcampusapp.component.tumui.calendar.model.CalendarItem
import de.tum.`in`.tumcampusapp.component.tumui.calendar.model.AbstractEvent
import de.tum.`in`.tumcampusapp.component.tumui.lectures.api.LecturesAPI
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.AbstractLecture
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.LectureAppointmentInterface
import de.tum.`in`.tumcampusapp.component.tumui.person.api.PersonAPI
import de.tum.`in`.tumcampusapp.component.tumui.person.model.PersonInterface
import de.tum.`in`.tumcampusapp.utils.*
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.URL

@Suppress("UNCHECKED_CAST")
class StudipClient(private val apiService: StudipAPIService, val context: Context, val converter: ResourceConverter) :
    LMSClient(),
    PersonAPI,
    CalendarAPI,
    LecturesAPI {
    private val userId
        get() = Utils.getSetting(context, Const.PROFILE_ID, "")

    override fun getIdentity(): PersonInterface {
        // Institutes not required
        return apiService.getIdentity().execute().body()!!
    }

    override fun searchPerson(query: String): List<PersonInterface> {
        // Institutes not required
        return apiService.searchPerson(query).execute().body()!!
    }

    override fun getPersonDetails(id: String): PersonInterface {
        val person = apiService.getPersonDetails(id).execute().body()!!
        person.institutes = getInstitutes(person.id)
        return person
    }

    private fun getInstitutes(id: String): List<StudipInstitute>? {
        return tryOrNull { apiService.getInstitutes(id).execute().body() } // TODO: Possibly to remove due to admin permission
    }

    /**
     * Returns only Kalendereinträge und nicht Stundenplan
     */
    override fun getCalendar(): List<AbstractEvent>? {
        if (userId.isEmpty()) throw UnauthorizedException()

        val jsonResponse = apiService.getCalendar(userId).execute().body()!!.bytes()

        return converter.readDocumentCollection(jsonResponse, StudipBaseEvent::class.java).get()
    }

    override fun createCalendarEvent(calendarItem: CalendarItem): String {
        // Not supported by the STUD.IP json api
        return ""
    }

    override fun deleteCalenderEvent(id: String) {
        // Not supported by the STUD.IP json api
    }

    override fun getPersonalLectures(): List<AbstractLecture> {
        return apiService.getPersonalLectures(userId).execute().body()!!
    }

    override fun getLectureDetails(id: String): AbstractLecture {
        return apiService.getLecture(id).execute().body()!!
    }

    override fun getLectureAppointments(id: String): List<LectureAppointmentInterface> {
        // Required as interfaces are slightly different
        return apiService.getLectureEvents(id).execute().body()!!.map { StudipLectureAppointment.fromStudipBaseEvent(it) }
    }

    override fun searchLectures(query: String): List<AbstractLecture> {
        return apiService.searchLectures(query).execute().body()!!
    }

    companion object {
        private var client: StudipClient? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): StudipClient {
            if (client == null) {
                client = buildAPIClient(context)
            }

            return client!!
        }

        private fun buildAPIClient(context: Context): StudipClient? {
            // TODO: Make CacheManager more generic
            val cacheManager = CacheManager(context)

            val client = ApiHelper.getOkHttpLMSClient(context)
                .newBuilder()
                .addInterceptor(CheckErrorInterceptor(context))
                .build()

            val objectMapper = ObjectMapper()
            // allow not used attributes
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // Skip null fields in json
            objectMapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP))
            // Skip assigned of `null` for any `String` property!
            // objectMapper.configOverride(String::class.java).setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP))

            val resourceConverter = ResourceConverter(
                objectMapper,
                StudipPerson::class.java,
                StudipInstitute::class.java,
                StudipCalendarEvent::class.java,
                StudipCourseEvent::class.java,
                StudipLecture::class.java,
            )

            // Set up relationship resolver
            resourceConverter.setGlobalResolver { relationshipURL ->
                val baseUrl = URL(ConfigUtils.getConfig(ConfigConst.API_BASE_URL, ""))
                val requestUrl = URL(baseUrl.protocol, baseUrl.host, baseUrl.port, relationshipURL)
                Utils.log("Request-Url: $requestUrl")

                val request = Request.Builder().url(requestUrl).build()
                client.newCall(request).execute().body!!.bytes()
            }

            val jsonapiConverterFactory = JSONAPIConverterFactory(resourceConverter)

            val apiService = Retrofit.Builder()
                .baseUrl(ConfigUtils.getConfig(ConfigConst.API_BASE_URL, ""))
                .client(client)
                .addConverterFactory(jsonapiConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(StudipAPIService::class.java)

            return StudipClient(apiService, context, resourceConverter)
        }
    }
}
