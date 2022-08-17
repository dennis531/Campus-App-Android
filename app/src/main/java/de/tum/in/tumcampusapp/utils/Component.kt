package de.tum.`in`.tumcampusapp.utils

import android.content.Context
import de.tum.`in`.tumcampusapp.R

/**
 * Each instance represents a component of the app
 *
 * @param titleResId Resource to the title
 * @param requiresAPI Is an api client required?
 * @param needsAuthentication Is an authenticated user for the api client needed?
 * @param preferenceKey Card preference key of the settings
 */
enum class Component(val titleResId: Int, val requiresAPI: Boolean = false, val needsAuthentication: Boolean = false, val preferenceKey: String? = null) {
    CALENDAR(R.string.calendar, true, true),
    GRADES(R.string.my_grades, true, true),
    LECTURES(R.string.my_lectures, true, true, "card_next_lecture"),
    PERSON(R.string.person_search, true, true),
    ROOMFINDER(R.string.roomfinder, true, true),
    TUITIONFEES(R.string.tuition_fees, ConfigUtils.shouldTuitionLoadedFromApi(), true, "card_tuition_fee"), // Api requires authenticated user
    CAFETERIA(R.string.cafeteria, true, preferenceKey = "card_cafeteria"),
    CHAT(R.string.chat, true, true, "card_chat"),
    EDUROAM(R.string.eduroam, false, preferenceKey = "card_eduroam"),
    GEOFENCING(R.string.geofencing, false),
    NEWS(R.string.news, true, true, "card_news"),
    ONBOARDING(R.string.onboarding, true),
    OPENINGHOUR(R.string.opening_hours, true, false),
    OVERVIEW(R.string.home),
    TRANSPORTATION(R.string.transport, true, preferenceKey = "card_transportation"),
    STUDYROOM(R.string.study_rooms, true),
    MESSAGES(R.string.messages, true, true, "card_messages");

    fun getTitle(context: Context): String {
        return context.getString(titleResId)
    }
}
