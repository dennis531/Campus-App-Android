package de.tum.`in`.tumcampusapp.utils

import android.content.Context
import de.tum.`in`.tumcampusapp.R

/**
 * Each instance represents a component of the app
 *
 * @param titleResId Resource to the title
 * @param requiresLMS Is a LMS api required?
 * @param needsLMSAccess Is an authenticated user for the LMS api needed?
 */
enum class Component(val titleResId: Int, val requiresLMS: Boolean = false, val needsLMSAccess: Boolean = false) {
    CALENDAR(R.string.calendar, true, true),
    GRADES(R.string.my_grades, true, true),
    LECTURES(R.string.my_lectures, true, true),
    PERSON(R.string.person_search, true, true),
    ROOMFINDER(R.string.roomfinder, true, true),
    TUITIONFEES(R.string.tuition_fees, ConfigUtils.shouldTuitionLoadedFromApi(), true), // Api requires authenticated user
    CAFETERIA(R.string.cafeteria, false),
    CHAT(R.string.chat, true, true),
    EDUROAM(R.string.eduroam, false),
    GEOFENCING(R.string.geofencing, false),
    NEWS(R.string.news, true, true),
    ONBOARDING(R.string.onboarding, true),
    OPENINGHOUR(R.string.opening_hours, true, false),
    OVERVIEW(R.string.home),
    TRANSPORTATION(R.string.transport),
    STUDYROOM(R.string.study_rooms, true),
    MESSAGES(R.string.messages, true, true);

    fun getTitle(context: Context): String {
        return context.getString(titleResId)
    }
}
