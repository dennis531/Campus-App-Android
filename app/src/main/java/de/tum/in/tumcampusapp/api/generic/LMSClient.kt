package de.tum.`in`.tumcampusapp.api.generic

import de.tum.`in`.tumcampusapp.component.tumui.calendar.api.CalendarAPI
import de.tum.`in`.tumcampusapp.component.tumui.lectures.api.LecturesAPI
import de.tum.`in`.tumcampusapp.component.tumui.person.api.PersonAPI
import de.tum.`in`.tumcampusapp.utils.Component

abstract class LMSClient : BaseAPI {
    fun hasAPI(component: Component): Boolean {
        return when (component) {
            Component.ONBOARDING -> true // BaseAPI contains Onboarding's Interface
            Component.CALENDAR -> this is CalendarAPI
            Component.LECTURES -> this is LecturesAPI
            Component.PERSON -> this is PersonAPI
            else -> false
        }
    }
}