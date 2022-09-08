package de.uos.campusapp.component.ui.lectures.model

import org.joda.time.DateTime

/**
 * Represents an lecture appointment
 *
 * @property id
 * @property title Name of appointment
 * @property dtstart Start time
 * @property dtend End time
 * @property type Type like "Vorlesung" or "Seminar" (optional)
 * @property location Location (optional)
 */
interface LectureAppointmentInterface {
    val id: String?
    val title: String?
    val dtstart: DateTime
    val dtend: DateTime
    val type: String?
    val location: String?
}