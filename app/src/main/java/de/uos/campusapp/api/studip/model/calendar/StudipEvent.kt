package de.uos.campusapp.api.studip.model.calendar

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type
import de.uos.campusapp.component.tumui.calendar.model.CalendarItemType
import de.uos.campusapp.component.tumui.calendar.model.AbstractEvent
import org.joda.time.DateTime

open class StudipBaseEvent: AbstractEvent() {
    @Id
    override val id: String? = null

    @JsonProperty("title")
    override val title: String = ""

    @JsonProperty("categories")
    val categories: List<String>? = null

    @JsonIgnore
    override var type: CalendarItemType? = null
        get() {
            return if(categories?.contains("Vorlesung") == true) CalendarItemType.LECTURE else CalendarItemType.OTHER
        }

    @JsonProperty("description")
    override val description: String? = null

    @JsonProperty("start")
    val start: String? = null
    override val dtstart: DateTime?
        get() = if (start != null) DateTime(start) else null

    @JsonProperty("end")
    val end: String? = null
    override val dtend: DateTime?
        get() = if (end != null) DateTime(end) else null

    @JsonProperty("location")
    override val location: String? = null

    @JsonIgnore
    override val isEditable: Boolean = false // Not supported by the STUD.IP json api
}

@Type("calendar-events")
class StudipCalendarEvent : StudipBaseEvent()

@Type("course-events")
class StudipCourseEvent : StudipBaseEvent()