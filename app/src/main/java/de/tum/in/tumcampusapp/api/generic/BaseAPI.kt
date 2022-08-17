package de.tum.`in`.tumcampusapp.api.generic

import de.tum.`in`.tumcampusapp.component.tumui.calendar.api.CalendarAPI
import de.tum.`in`.tumcampusapp.component.tumui.grades.api.GradesAPI
import de.tum.`in`.tumcampusapp.component.tumui.lectures.api.LecturesAPI
import de.tum.`in`.tumcampusapp.component.tumui.person.api.PersonAPI
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.api.RoomFinderAPI
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.api.generic.CafeteriaAPI
import de.tum.`in`.tumcampusapp.component.ui.chat.api.ChatAPI
import de.tum.`in`.tumcampusapp.component.ui.messages.api.MessagesAPI
import de.tum.`in`.tumcampusapp.component.ui.news.api.NewsAPI
import de.tum.`in`.tumcampusapp.component.ui.onboarding.api.OnboardingAPI
import de.tum.`in`.tumcampusapp.component.ui.openinghours.api.OpeningHoursAPI
import de.tum.`in`.tumcampusapp.component.ui.studyroom.api.StudyRoomAPI
import de.tum.`in`.tumcampusapp.component.ui.transportation.api.generic.TransportationAPI
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.Utils

interface BaseAPI {
    fun hasAPI(component: Component): Boolean {
        val apiClazz = COMPONENT_API[component]
        val hasApi = apiClazz?.isInstance(this) ?: false

        if (!hasApi) {
            Utils.log("Client does not implement the required api ${apiClazz?.simpleName ?: ""} for component ${component.name}")
        }

        return hasApi
    }

    companion object {
        private val COMPONENT_API = mapOf(
            Component.CAFETERIA to CafeteriaAPI::class.java,
            Component.CALENDAR to CalendarAPI::class.java,
            Component.CHAT to ChatAPI::class.java,
            Component.GRADES to GradesAPI::class.java,
            Component.LECTURES to LecturesAPI::class.java,
            Component.MESSAGES to MessagesAPI::class.java,
            Component.NEWS to NewsAPI::class.java,
            Component.ONBOARDING to OnboardingAPI::class.java,
            Component.OPENINGHOUR to OpeningHoursAPI::class.java,
            Component.PERSON to PersonAPI::class.java,
            Component.ROOMFINDER to RoomFinderAPI::class.java,
            Component.STUDYROOM to StudyRoomAPI::class.java,
            Component.TRANSPORTATION to TransportationAPI::class.java,
        )
    }
}