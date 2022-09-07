package de.uos.campusapp.api.studip

import android.content.Context
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import de.uos.campusapp.api.general.ApiHelper
import de.uos.campusapp.api.general.exception.UnauthorizedException
import de.uos.campusapp.component.ui.onboarding.api.OnboardingAPI
import de.uos.campusapp.api.studip.interceptors.CheckErrorInterceptor
import de.uos.campusapp.api.studip.model.calendar.StudipBaseEvent
import de.uos.campusapp.api.studip.model.calendar.StudipCalendarEvent
import de.uos.campusapp.api.studip.model.calendar.StudipCourseEvent
import de.uos.campusapp.api.studip.model.chat.StudipBlubberComment
import de.uos.campusapp.api.studip.model.chat.StudipBlubberThread
import de.uos.campusapp.api.studip.model.lectures.StudipLecture
import de.uos.campusapp.api.studip.model.lectures.StudipLectureAppointment
import de.uos.campusapp.api.studip.model.lectures.StudipLectureFile
import de.uos.campusapp.api.studip.model.messages.StudipMessage
import de.uos.campusapp.api.studip.model.news.StudipNews
import de.uos.campusapp.api.studip.model.person.StudipInstitute
import de.uos.campusapp.api.studip.model.person.StudipPerson
import de.uos.campusapp.component.tumui.calendar.api.CalendarAPI
import de.uos.campusapp.component.tumui.calendar.model.CalendarItem
import de.uos.campusapp.component.tumui.calendar.model.AbstractEvent
import de.uos.campusapp.component.tumui.grades.api.GradesAPI
import de.uos.campusapp.component.tumui.grades.model.AbstractExam
import de.uos.campusapp.component.tumui.grades.model.Exam
import de.uos.campusapp.component.tumui.lectures.api.LecturesAPI
import de.uos.campusapp.component.tumui.lectures.model.AbstractLecture
import de.uos.campusapp.component.tumui.lectures.model.FileInterface
import de.uos.campusapp.component.tumui.lectures.model.LectureAppointmentInterface
import de.uos.campusapp.component.tumui.person.api.PersonAPI
import de.uos.campusapp.component.tumui.person.model.PersonInterface
import de.uos.campusapp.component.tumui.roomfinder.api.RoomFinderAPI
import de.uos.campusapp.component.tumui.roomfinder.model.*
import de.uos.campusapp.component.ui.chat.api.ChatAPI
import de.uos.campusapp.component.ui.chat.model.AbstractChatMessage
import de.uos.campusapp.component.ui.chat.model.ChatMember
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom
import de.uos.campusapp.component.ui.messages.api.MessagesAPI
import de.uos.campusapp.component.ui.messages.model.AbstractMessage
import de.uos.campusapp.component.ui.messages.model.AbstractMessageMember
import de.uos.campusapp.component.ui.messages.model.MessageMember
import de.uos.campusapp.component.ui.messages.model.MessageType
import de.uos.campusapp.component.ui.news.api.NewsAPI
import de.uos.campusapp.component.ui.news.model.AbstractNews
import de.uos.campusapp.component.ui.onboarding.model.IdentityInterface
import de.uos.campusapp.component.ui.openinghours.api.OpeningHoursAPI
import de.uos.campusapp.component.ui.openinghours.model.AbstractLocation
import de.uos.campusapp.component.ui.openinghours.model.Location
import de.uos.campusapp.component.ui.studyroom.api.StudyRoomAPI
import de.uos.campusapp.component.ui.studyroom.model.*
import de.uos.campusapp.config.Api
import de.uos.campusapp.utils.*
import okhttp3.Request
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.InputStream
import java.net.URL

class StudipClient(private val apiService: StudipAPIService, context: Context, val converter: ResourceConverter) :
    OnboardingAPI,
    PersonAPI,
    CalendarAPI,
    LecturesAPI,
    NewsAPI,
    GradesAPI,
    RoomFinderAPI,
    ChatAPI,
    OpeningHoursAPI,
    StudyRoomAPI,
    MessagesAPI {

    private var userId = Utils.getSetting(context, Const.PROFILE_ID, "")

    override fun getGrades(): List<AbstractExam> {
        val exam1 = Exam(
            "123",
            "Info A",
            "WS 2016",
        )

        val exam2 = Exam(
            "456",
            "Info D",
            "SS 2018",
            DateTime(2018, 6, 23, 10, 0),
            "Prof. Dr. Chimani",
            3.0,
            "Klausur",
            "B.Sc. Informatik"
        )

        val exam3 = Exam(
            "789",
            "Info C",
            "SS 2018",
            DateTime(2018, 6, 23, 10, 0),
            "Prof. Dr. Porrmann",
            1.0,
            "Klausur",
            "B.Sc. Informatik"
        )

        val exam4 = Exam(
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
            "42/E04",
            "Heger-Tor-Wall 12",
            "Campus Innenstadt",
            "Zentrum für Digitale Lehre, Campus-Management und Hochschuldidaktik (virtUOS)",
            "https://www.uni-osnabrueck.de/fileadmin/documents/public/6_presse_oeffentlichkeit/6.6_lageplaene/Lageplaene_Innenstadt_2022-04.jpg"
        )

        val room2 = RoomFinderRoom(
            "456",
            "42/E01"
        )
        return listOf(room1, room2)
    }

    override fun fetchRoomSchedule(room: RoomFinderRoomInterface): List<RoomFinderScheduleInterface>? {
        val schedule1 = RoomFinderSchedule("1", "Präsentation", DateTime.now(), DateTime.now().plusHours(2))
        val schedule2 = RoomFinderSchedule("2", "Versammlung", DateTime.now().plusHours(4), DateTime.now().plusHours(6))
        return listOf(schedule1, schedule2)
    }

    override fun fetchRoomCoordinates(room: RoomFinderRoomInterface): RoomFinderCoordinateInterface? {
        return RoomFinderCoordinate("52.2725028", "8.041081")
    }

    override fun getIdentity(): IdentityInterface {
        // Institutes not required
        val person = apiService.getIdentity().execute().body()!!
        // update userId
        userId = person.id

        // Convert StudipPerson to Identity instance
        return person.toIdentity()
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
     * Returns only Kalendereinträge and not Stundenplan
     */
    override fun getCalendar(): List<AbstractEvent>? {
        if (userId.isEmpty()) throw UnauthorizedException()

        val jsonResponseStream = apiService.getCalendar(userId).execute().body()!!.byteStream()

        return converter.readDocumentCollection(jsonResponseStream, StudipBaseEvent::class.java).get()
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

    override fun searchLectures(query: String): List<AbstractLecture> {
        return apiService.searchLectures(query).execute().body()!!
    }

    override fun getLectureDetails(id: String): AbstractLecture {
        return apiService.getLecture(id).execute().body()!!
    }

    override fun getLectureAppointments(id: String): List<LectureAppointmentInterface> {
        // Required as interfaces are slightly different
        return apiService.getLectureEvents(id).execute().body()!!.map { StudipLectureAppointment.fromStudipBaseEvent(it) }
    }

    override fun getLectureFiles(id: String): List<FileInterface>? {
        // Only show downloadable files
        return apiService.getLectureFiles(id).execute().body()!!
            .filter { it.isDownloadable }
    }

    override fun downloadLectureFile(file: FileInterface): InputStream? {
        return apiService.downloadLectureFile(file.id).execute().body()!!.byteStream()
    }

    override fun getNews(): List<AbstractNews> {
        return apiService.getNews().execute().body()!!
    }

    override fun getChatRooms(): List<AbstractChatRoom> {
        return apiService.getChatRooms().execute().body()!!
    }

    override fun createChatRoom(chatRoom: AbstractChatRoom): AbstractChatRoom? {
        // Not supported by the STUD.IP json api
        return null
    }

    override fun leaveChatRoom(chatRoom: AbstractChatRoom) {
        // Not supported by the STUD.IP json api
    }

    override fun getChatMessages(chatRoom: AbstractChatRoom, latestMessage: AbstractChatMessage?): List<AbstractChatMessage> {
        if (latestMessage != null) {
            val oldMessages =  apiService.getOlderChatMessages(chatRoom.id, latestMessage.timestamp.toString()).execute().body()!!.toMutableList()

            // Remove duplicate lastMessage from response
            if (oldMessages.isNotEmpty()) {
                oldMessages.removeAt(0)
            }

            return oldMessages
        }

        return apiService.getChatMessages(chatRoom.id).execute().body()!!
    }

    override fun sendChatMessage(chatRoom: AbstractChatRoom, message: AbstractChatMessage): AbstractChatMessage {
        return apiService.sendChatMessage(chatRoom.id, StudipBlubberComment(message)).execute().body()!!
    }

    override fun searchChatMember(query: String): List<ChatMember>? {
        // Not needed as chat member can not be added to chat rooms through the STUD.IP json api
        return null
    }

    override fun addMemberToChatRoom(chatRoom: AbstractChatRoom, member: ChatMember): AbstractChatRoom? {
        // Not supported by the STUD.IP json api
        return null
    }

    override fun getOpeningHours(): List<AbstractLocation> {
        val location1 = Location(
            "1",
            "Bibliothek",
            "Bibliothek Westerberg",
            "12 - 14 Uhr",
            "Barbara Straße 1",
            "",
            "",
            "Bücher aus der Naturwissenschaft.",
            "https://www.ub.uni-osnabrueck.de/startseite.html")

        val location2 = Location(
            "2",
            "Bibliothek",
            "Bibliothek Schlossgarten",
            "12 - 14 Uhr",
            "Schloss Straße 1",
            "",
            "",
            "Bücher aus den Wirtschafswissenschaften.",
            "https://www.ub.uni-osnabrueck.de/startseite.html")

        val location3 = Location(
            "3",
            "Fachbereiche",
            "Institut der Informatik",
            "12 - 14 Uhr",
            "Wachsbleiche 1",
            "50/E01",
            "Altstadt",
            "",
            "https://www.informatik.uni-osnabrueck.de/institut_fuer_informatik.html")

        return listOf(location1, location2, location3)
    }

    override fun getStudyRoomGroups(): List<AbstractStudyRoomGroup> {
        val studyRoom1 = StudyRoom(
            "1",
            "96/E01",
            "Einzelarbeitsraum",
            occupiedUntil = DateTime.now().plusHours(2)
        )

        val studyRoom2 = StudyRoom(
            "2",
            "96/E02",
            "Gruppenarbeitsraum",
            freeUntil = DateTime.now().plusHours(3)
        )

        val studyRoomGroup1 = StudyRoomGroup(
            "1",
            "Bibliothek Westerberg",
            listOf(studyRoom1, studyRoom2)
        )

        val studyRoom3 = StudyRoom(
            "3",
            "21/E01",
            "Einzelarbeitsraum",
        )

        val studyRoom4 = StudyRoom(
            "4",
            "21/E02",
            "Gruppenarbeitsraum",
        )

        val studyRoom5 = StudyRoom(
            "5",
            "21/E03",
            "Gruppenarbeitsraum 2",
            occupiedUntil = DateTime.now().plusHours(2)
        )

        val studyRoom6 = StudyRoom(
            "6",
            "21/E04",
            "Gruppenarbeitsraum",
            freeUntil = DateTime.now().plusHours(1)
        )

        val studyRoomGroup2 = StudyRoomGroup(
            "2",
            "Bibliothek Innenstadt",
            listOf(studyRoom3, studyRoom4, studyRoom5, studyRoom6)
        )

        return listOf(studyRoomGroup1, studyRoomGroup2)
    }

    override fun getMessages(): List<AbstractMessage> {
        val inboxMessages = apiService.getInboxMessages(userId).execute().body()!!
            .onEach {
                it.type = MessageType.INBOX
            }

        val outboxMessages = apiService.getOutboxMessages(userId).execute().body()!!
            .onEach {
                it.type = MessageType.SENT
            }

        return inboxMessages + outboxMessages
    }

    override fun sendMessage(message: AbstractMessage): AbstractMessage {
        return apiService.sendMessage(StudipMessage(message)).execute().body()!!
    }

    override fun deleteMessage(message: AbstractMessage) {
        apiService.deleteMessage(message.id).execute()
    }

    override fun searchRecipient(query: String): List<AbstractMessageMember> {
        return apiService.searchPerson(query).execute().body()!!
            .map { MessageMember(it.id, it.fullName) }
    }

    companion object {
        private var client: StudipClient? = null

        private val STUDIP_BASE_URL = ConfigUtils.getConfig(ConfigConst.STUDIP_API_BASE_URL, "")

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): StudipClient {
            if (client == null) {
                client = buildAPIClient(context)
            }

            return client!!
        }

        private fun buildAPIClient(context: Context): StudipClient {
            // TODO: Make CacheManager more generic
            val cacheManager = CacheManager(context)

            val client = ApiHelper.getOkHttpAuthClient(context, Api.STUDIP)
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
                StudipLectureFile::class.java,
                StudipNews::class.java,
                StudipBlubberThread::class.java,
                StudipBlubberComment::class.java,
                StudipMessage::class.java,
            )

            // Set up relationship resolver
            resourceConverter.setGlobalResolver { relationshipURL ->
                val baseUrl = URL(STUDIP_BASE_URL)
                val requestUrl = URL(baseUrl.protocol, baseUrl.host, baseUrl.port, relationshipURL)
                Utils.log("Request-Url: $requestUrl")

                val request = Request.Builder().url(requestUrl).build()
                client.newCall(request).execute().body!!.bytes()
            }

            val jsonapiConverterFactory = JSONAPIConverterFactory(resourceConverter)

            val apiService = Retrofit.Builder()
                .baseUrl(STUDIP_BASE_URL)
                .client(client)
                .addConverterFactory(jsonapiConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(StudipAPIService::class.java)

            return StudipClient(apiService, context, resourceConverter)
        }
    }
}
