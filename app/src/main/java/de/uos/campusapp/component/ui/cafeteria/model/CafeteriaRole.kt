package de.uos.campusapp.component.ui.cafeteria.model

import androidx.annotation.StringRes
import de.uos.campusapp.R

/**
 * Defines cafeteria roles for the cafeteria menu prices
 */
enum class CafeteriaRole(val id: Int, @StringRes val nameResId: Int) {
    STUDENT(0, R.string.student),
    EMPLOYEE(1, R.string.employee),
    GUEST(2, R.string.guest);
    // Add more if needed

    companion object {
        private val map = values().associateBy(CafeteriaRole::id)
        fun fromId(id: Int) = map[id] ?: STUDENT
    }
}