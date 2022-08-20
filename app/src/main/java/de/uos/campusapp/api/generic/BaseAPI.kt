package de.uos.campusapp.api.generic

import de.uos.campusapp.component.tumui.calendar.api.CalendarAPI
import de.uos.campusapp.component.tumui.grades.api.GradesAPI
import de.uos.campusapp.component.tumui.lectures.api.LecturesAPI
import de.uos.campusapp.component.tumui.person.api.PersonAPI
import de.uos.campusapp.component.tumui.roomfinder.api.RoomFinderAPI
import de.uos.campusapp.component.ui.cafeteria.api.generic.CafeteriaAPI
import de.uos.campusapp.component.ui.chat.api.ChatAPI
import de.uos.campusapp.component.ui.messages.api.MessagesAPI
import de.uos.campusapp.component.ui.news.api.NewsAPI
import de.uos.campusapp.component.ui.onboarding.api.OnboardingAPI
import de.uos.campusapp.component.ui.openinghours.api.OpeningHoursAPI
import de.uos.campusapp.component.ui.studyroom.api.StudyRoomAPI
import de.uos.campusapp.component.ui.transportation.api.generic.TransportationAPI
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.Utils

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