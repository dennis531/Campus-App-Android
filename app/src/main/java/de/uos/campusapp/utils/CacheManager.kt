package de.uos.campusapp.utils

import android.content.Context
import de.uos.campusapp.component.ui.calendar.CalendarController
import de.uos.campusapp.component.ui.calendar.api.CalendarAPI
import de.uos.campusapp.component.ui.lectures.api.LecturesAPI
import de.uos.campusapp.component.ui.chat.ChatRoomController
import de.uos.campusapp.service.QueryLocationsService
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

    @Synchronized
    fun clearCache() {
        cache.delete()
    }
}