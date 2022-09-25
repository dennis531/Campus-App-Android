package de.uos.campusapp.component.ui.lectures.model

import org.joda.time.DateTime

/**
 * Simple implementation of [LectureSemesterInterface]
 */
data class LectureSemester(
    override val title: String,
    override val startDate: DateTime?
) : LectureSemesterInterface
