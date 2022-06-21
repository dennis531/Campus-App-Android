package de.tum.`in`.tumcampusapp.component.tumui.lectures.model

import org.joda.time.DateTime

interface LectureAppointmentInterface {
    val id: String?
    val title: String?
    val dtstart: DateTime
    val dtend: DateTime
    val type: String?
    val location: String?
}