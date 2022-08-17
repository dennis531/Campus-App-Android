package de.tum.`in`.tumcampusapp.utils

import android.content.Context
import de.tum.`in`.tumcampusapp.component.tumui.calendar.CalendarController
import de.tum.`in`.tumcampusapp.component.tumui.calendar.api.CalendarAPI
import de.tum.`in`.tumcampusapp.component.tumui.lectures.api.LecturesAPI
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatRoomController
import de.tum.`in`.tumcampusapp.service.QueryLocationsService
import okhttp3.Cache
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class CacheManager @Inject constructor(private val context: Context) {

    private val calendarApiClient: CalendarAPI by lazy {
        ConfigUtils.getApiClient(context, Component.CALENDAR) as CalendarAPI
    }

    private val lecturesApiClient: LecturesAPI by lazy {
        ConfigUtils.getApiClient(context, Component.LECTURES) as LecturesAPI
    }

    val cache: Cache
        get() = Cache(context.cacheDir, 10 * 1024 * 1024) // 10 MB

    fun fillCache() {
        doAsync {
            syncCalendar()
            syncPersonalLectures()
        }
    }

    private fun syncCalendar() {
        if (!ConfigUtils.isComponentEnabled(context, Component.CALENDAR)) {
            return
        }

        try {
            val events = calendarApiClient.getCalendar() ?: return
            CalendarController(context).importCalendar(events)
            loadRoomLocations()
        } catch (t: Throwable) {
            Utils.log(t, "Error while loading calendar in CacheManager")
        }
    }

    private fun loadRoomLocations() {
        doAsync {
            QueryLocationsService.enqueueWork(context)
        }
    }

    private fun syncPersonalLectures() {
        if(!ConfigUtils.isComponentEnabled(context, Component.LECTURES) || !ConfigUtils.isComponentEnabled(context, Component.CHAT)) {
            return
        }

        try {
            val lectures = lecturesApiClient.getPersonalLectures()
            val chatRoomController = ChatRoomController(context)
//            chatRoomController.createLectureRooms(lectures) // TODO: Enable after chat is generalized
            Utils.log("Successfully updated personal lectures in background")
        } catch (t: Throwable) {
            Utils.log(t, "Error loading personal lectures in background")
        }
    }

    @Synchronized
    fun clearCache() {
        cache.delete()
    }
}