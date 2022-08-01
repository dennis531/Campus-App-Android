package de.tum.`in`.tumcampusapp.utils

import android.content.Context
import de.tum.`in`.tumcampusapp.R

enum class Component(val titleResId: Int, val requiresLMS: Boolean) {
    CALENDAR(R.string.calendar, true),
    GRADES(R.string.my_grades, true),
    LECTURES(R.string.my_lectures, true),
    PERSON(R.string.person_search, true),
    ROOMFINDER(R.string.roomfinder, true),
    TUITIONFEES(R.string.tuition_fees, false),
    CAFETERIA(R.string.cafeteria, false),
    CHAT(R.string.chat, true),
    EDUROAM(R.string.eduroam, false),
    NEWS(R.string.news, true),
    ONBOARDING(R.string.onboarding, true),
    OPENINGHOUR(R.string.opening_hours, false),
    OVERVIEW(R.string.home, false),
    TRANSPORTATION(R.string.transport, false),
    STUDYROOM(R.string.study_rooms, true);

    fun getTitle(context: Context): String {
        return context.getString(titleResId)
    }
}