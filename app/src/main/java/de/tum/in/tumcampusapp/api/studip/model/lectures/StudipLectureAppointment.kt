package de.tum.`in`.tumcampusapp.api.studip.model.lectures

import de.tum.`in`.tumcampusapp.api.studip.model.calendar.StudipBaseEvent
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.LectureAppointmentInterface
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