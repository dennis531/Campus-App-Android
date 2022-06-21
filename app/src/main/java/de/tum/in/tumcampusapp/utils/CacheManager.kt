package de.tum.`in`.tumcampusapp.utils

import android.content.Context
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.service.QueryLocationsService
import okhttp3.Cache
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class CacheManager @Inject constructor(private val context: Context) {

    @Inject
    lateinit var apiClient: LMSClient

    val cache: Cache
        get() = Cache(context.cacheDir, 10 * 1024 * 1024) // 10 MB

    fun fillCache() {
        doAsync {
            syncCalendar()
            syncPersonalLectures()
        }
    }

    private fun syncCalendar() {
//        (apiClient as? CalenderAPI)?.let {
//            it.getCalendar(CacheControl.USE_CACHE)
//            .enqueue(object : Callback<EventsResponse> {
//                override fun onResponse(call: Call<EventsResponse>, response: Response<EventsResponse>) {
//                    val eventsResponse = response.body() ?: return
//                    val events = eventsResponse.events ?: return
//                    CalendarController(context).importCalendar(events)
//                    loadRoomLocations()
//                }
//
//                override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
//                    Utils.log(t, "Error while loading calendar in CacheManager")
//                }
//            })
//        }
    }

    private fun loadRoomLocations() {
        doAsync {
            QueryLocationsService.enqueueWork(context)
        }
    }

    private fun syncPersonalLectures() {
//        (apiClient as? LecturesAPI)?.let {
//            it.getInstance(context)
//                .getPersonalLectures(CacheControl.USE_CACHE)
//                .enqueue(object : Callback<LecturesResponse> {
//                    override fun onResponse(
//                        call: Call<LecturesResponse>,
//                        response: Response<LecturesResponse>
//                    ) {
//                        Utils.log("Successfully updated personal lectures in background")
//                        val lectures = response.body()?.lectures ?: return
//                        val chatRoomController = ChatRoomController(context)
//                        chatRoomController.createLectureRooms(lectures)
//                    }
//
//                    override fun onFailure(call: Call<LecturesResponse>, t: Throwable) {
//                        Utils.log(t, "Error loading personal lectures in background")
//                    }
//                })
//        }
    }

    @Synchronized
    fun clearCache() {
        cache.delete()
    }
}