package de.uos.campusapp.component.ui.lectures.model

import org.joda.time.DateTime

/**
 * Represents a semester or multiple semesters
 *
 * @property title Semester name oder bundled semester names
 * @property startDate Start of first semester, used for sorting lectures (optional)
 */
interface LectureSemesterInterface {
    val title: String
    val startDate: DateTime?
}
