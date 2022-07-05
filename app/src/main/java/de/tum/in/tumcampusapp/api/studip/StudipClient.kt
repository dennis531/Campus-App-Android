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
import de.tum.`in`.tumcampusapp.api.studip.model.grades.StudipExam
import de.tum.`in`.tumcampusapp.api.studip.model.lectures.StudipLecture
import de.tum.`in`.tumcampusapp.api.studip.model.lectures.StudipLectureAppointment
import de.tum.`in`.tumcampusapp.api.studip.model.news.StudipNews
import de.tum.`in`.tumcampusapp.api.studip.model.person.StudipInstitute
import de.tum.`in`.tumcampusapp.api.studip.model.person.StudipPerson
import de.tum.`in`.tumcampusapp.api.studip.model.roomfinder.StudipRoomCoordinate
import de.tum.`in`.tumcampusapp.api.studip.model.roomfinder.StudipRoomSchedule
import de.tum.`in`.tumcampusapp.component.tumui.calendar.api.CalendarAPI
import de.tum.`in`.tumcampusapp.component.tumui.calendar.model.CalendarItem
import de.tum.`in`.tumcampusapp.component.tumui.calendar.model.AbstractEvent
import de.tum.`in`.tumcampusapp.component.tumui.grades.api.GradesAPI
import de.tum.`in`.tumcampusapp.component.tumui.grades.model.AbstractExam
import de.tum.`in`.tumcampusapp.component.tumui.lectures.api.LecturesAPI
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.AbstractLecture
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.LectureAppointmentInterface
import de.tum.`in`.tumcampusapp.component.tumui.person.api.PersonAPI
import de.tum.`in`.tumcampusapp.component.tumui.person.model.PersonInterface
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.api.RoomFinderAPI
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderCoordinateInterface
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderRoom
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderRoomInterface
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderScheduleInterface
import de.tum.`in`.tumcampusapp.component.ui.news.api.NewsAPI
import de.tum.`in`.tumcampusapp.component.ui.news.model.AbstractNews
import de.tum.`in`.tumcampusapp.utils.*
import okhttp3.Request
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.URL

@Suppress("UNCHECKED_CAST")
class StudipClient(private val apiService: StudipAPIService, context: Context, val converter: ResourceConverter) :
    LMSClient(),
    PersonAPI,
    CalendarAPI,
    LecturesAPI,
    NewsAPI,
    GradesAPI,
    RoomFinderAPI {
    private var userId = Utils.getSetting(context, Const.PROFILE_ID, "")

    override fun getGrades(): List<AbstractExam> {
        val exam1 = StudipExam(
            "123",
            "Info A",
            "WS 2016",
        )

        val exam2 = StudipExam(
            "456",
            "Info D",
            "SS 2018",
            DateTime(2018, 6, 23, 10, 0),
            "Prof. Dr. Chimani",
            3.0,
            "Klausur",
            "B.Sc. Informatik"
        )

        val exam3 = StudipExam(
            "789",
            "Info C",
            "SS 2018",
            DateTime(2018, 6, 23, 10, 0),
            "Prof. Dr. Porrmann",
            1.0,
            "Klausur",
            "B.Sc. Informatik"
        )

        val exam4 = StudipExam(
            "789",
            "Info B",
            "SS 2018",
            DateTime(2018, 6, 23, 10, 0),
            "Prof. Dr. Pulvermüller",
            1.0,
            "Klausur",
            "B.Sc. Informatik"
        )

        return listOf(exam1, exam2, exam3, exam4)
    }

    override fun searchRooms(query: String): List<RoomFinderRoomInterface> {
        val room1 = RoomFinderRoom(
            "123",
            "12",
            "42/E04",
            "Heger-Tor-Wall 12",
            "Campus Innenstadt",
            "Zentrum für Digitale Lehre, Campus-Management und Hochschuldidaktik (virtUOS)",
            "https://www.uni-osnabrueck.de/fileadmin/documents/public/6_presse_oeffentlichkeit/6.6_lageplaene/Lageplaene_Innenstadt_2022-04.jpg"
        )

        val room2 = RoomFinderRoom(
            "456",
            "12",
            "42/E01",
            null,
            null,
            null,
            null
        )
        return listOf(room1, room2)
    }

    override fun fetchRoomSchedule(room: RoomFinderRoomInterface): List<RoomFinderScheduleInterface>? {
        val schedule1 = StudipRoomSchedule("1", "Präsentation", DateTime.now(), DateTime.now().plusHours(2))
        val schedule2 = StudipRoomSchedule("2", "Versammlung", DateTime.now().plusHours(4), DateTime.now().plusHours(6))
        return listOf(schedule1, schedule2)
    }

    override fun fetchRoomCoordinates(room: RoomFinderRoomInterface): RoomFinderCoordinateInterface? {
        return StudipRoomCoordinate("52.2725028", "8.041081")
    }

    override fun getIdentity(): PersonInterface {
        // Institutes not required
        val identity = apiService.getIdentity().execute().body()!!
        // update userId
        userId = identity.id

        return identity
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

    override fun getNews(): List<AbstractNews> {
        return apiService.getNews().execute().body()!!
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
                StudipNews::class.java,
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
