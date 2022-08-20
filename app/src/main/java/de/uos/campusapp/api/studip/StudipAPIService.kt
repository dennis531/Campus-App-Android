package de.uos.campusapp.api.studip

import de.uos.campusapp.api.studip.model.calendar.StudipCourseEvent
import de.uos.campusapp.api.studip.model.chat.StudipBlubberComment
import de.uos.campusapp.api.studip.model.chat.StudipBlubberThread
import de.uos.campusapp.api.studip.model.lectures.StudipLecture
import de.uos.campusapp.api.studip.model.messages.StudipMessage
import de.uos.campusapp.api.studip.model.news.StudipNews
import de.uos.campusapp.api.studip.model.person.StudipInstitute
import de.uos.campusapp.api.studip.model.person.StudipPerson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface StudipAPIService {

    // curl --request GET --url localhost/studip/jsonapi.php/v1/users/me?include=institute-memberships.institute --header "Authorization: Basic `echo -ne "test_dozent:testing" |base64`"
    @GET("users/me")
    fun getIdentity(): Call<StudipPerson>

    @GET("users")
    fun searchPerson(@Query("filter[search]") query: String): Call<List<StudipPerson>>

    @GET("users/{id}")
    fun getPersonDetails(@Path("id") id: String): Call<StudipPerson>

    @GET("users/{id}/institute-memberships?include=institute")
    fun getInstitutes(@Path("id") userId: String): Call<List<StudipInstitute>>

    @GET("users/{id}/events")
    fun getCalendar(@Path("id") id: String): Call<ResponseBody>

    @GET("users/{id}/courses?include=institute,start-semester,end-semester")
    fun getPersonalLectures(@Path("id") id: String): Call<List<StudipLecture>>

    @GET("courses/{id}?include=institute,start-semester,end-semester")
    fun getLecture(@Path("id") id: String): Call<StudipLecture>

    @GET("courses/{id}/events?page[limit]=100")
    fun getLectureEvents(@Path("id") id: String): Call<List<StudipCourseEvent>>

    @GET("courses?include=institute,start-semester,end-semester")
    fun searchLectures(@Query("filter[q]") query: String): Call<List<StudipLecture>>


    @GET("studip/news")
    fun getNews(): Call<List<StudipNews>>


    @GET("blubber-threads?include=context,mentions")
    fun getChatRooms(): Call<List<StudipBlubberThread>>

    @GET("blubber-threads/{id}/comments?include=author")
    fun getChatMessages(@Path("id") id: String): Call<List<StudipBlubberComment>>

    @GET("blubber-threads/{id}/comments?include=author")
    fun getOlderChatMessages(@Path("id") id: String, @Query("filter[before]") before: String): Call<List<StudipBlubberComment>>

    @POST("blubber-threads/{id}/comments")
    fun sendChatMessage(@Path("id") roomId: String, @Body message: StudipBlubberComment): Call<StudipBlubberComment>


    @GET("users/{id}/inbox?include=sender,recipients")
    fun getInboxMessages(@Path("id") userId: String): Call<List<StudipMessage>>

    @GET("users/{id}/outbox?include=sender,recipients")
    fun getOutboxMessages(@Path("id") userId: String): Call<List<StudipMessage>>

    @POST("messages")
    fun sendMessage(@Body message: StudipMessage): Call<StudipMessage>

    @DELETE("messages/{id}")
    fun deleteMessage(@Path("id") id: String): Call<ResponseBody>
}