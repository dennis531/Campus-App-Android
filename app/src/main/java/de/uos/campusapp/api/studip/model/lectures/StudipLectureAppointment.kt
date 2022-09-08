package de.uos.campusapp.api.studip.model.lectures

import de.uos.campusapp.api.studip.model.calendar.StudipBaseEvent
import de.uos.campusapp.component.ui.lectures.model.LectureAppointmentInterface
import org.joda.time.DateTime

class StudipLectureAppointment(
    override val id: String?,
    override val title: String?,
    override val type: String?,
    override val dtstart: DateTime,
    override val dtend: DateTime,
    override val location: String?
) : LectureAppointmentInterface {

    companion object {
        @JvmStatic
        fun fromStudipBaseEvent(event: StudipBaseEvent): StudipLectureAppointment {
            return StudipLectureAppointment(event.id, event.title, event.categories?.firstOrNull(), event.dtstart ?: DateTime(), event.dtend ?: DateTime(), event.location)
        }
    }
}