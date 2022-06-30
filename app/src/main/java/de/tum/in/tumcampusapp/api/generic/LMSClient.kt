package de.tum.`in`.tumcampusapp.api.generic

import de.tum.`in`.tumcampusapp.component.tumui.calendar.api.CalendarAPI
import de.tum.`in`.tumcampusapp.component.tumui.grades.api.GradesAPI
import de.tum.`in`.tumcampusapp.component.tumui.lectures.api.LecturesAPI
import de.tum.`in`.tumcampusapp.component.tumui.person.api.PersonAPI
import de.tum.`in`.tumcampusapp.component.ui.news.api.NewsAPI
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.Utils

abstract class LMSClient : BaseAPI {
    fun hasAPI(component: Component): Boolean {
        // BaseAPI contains Onboarding's Interface
        if (component == Component.ONBOARDING) {
            return true
        }

        val apiClazz = COMPONENT_API[component]
        val hasApi = apiClazz?.isInstance(this) ?: false

        if (!hasApi) {
            Utils.log("LMS Client does not implement the required api ${apiClazz?.simpleName ?: ""} for component ${component.name}")
        }

        return hasApi
    }

    companion object {
        private val COMPONENT_API = mapOf(
            Component.CALENDAR to CalendarAPI::class.java,
            Component.LECTURES to LecturesAPI::class.java,
            Component.NEWS to NewsAPI::class.java,
            Component.PERSON to PersonAPI::class.java,
            Component.GRADES to GradesAPI::class.java,
        )
    }
}