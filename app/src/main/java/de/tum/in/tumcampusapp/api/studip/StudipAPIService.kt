package de.tum.`in`.tumcampusapp.api.studip

import de.tum.`in`.tumcampusapp.api.studip.model.calendar.StudipCourseEvent
import de.tum.`in`.tumcampusapp.api.studip.model.lectures.StudipLecture
import de.tum.`in`.tumcampusapp.api.studip.model.person.StudipInstitute
import de.tum.`in`.tumcampusapp.api.studip.model.person.StudipPerson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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
}