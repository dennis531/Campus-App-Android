package de.uos.campusapp.api.studip.model.lectures

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import de.uos.campusapp.api.studip.model.person.InstituteRelationship
import de.uos.campusapp.component.ui.lectures.model.AbstractLecture
import de.uos.campusapp.component.ui.lectures.model.LectureSemester
import de.uos.campusapp.component.ui.lectures.model.LectureSemesterInterface
import org.joda.time.DateTime

@Type("courses")
class StudipLecture : AbstractLecture() {
    @Id
    override val id: String = ""

    @JsonProperty("title")
    override val title: String = ""

    @Relationship("start-semester")
    private val startSemester: StudipSemester? = null

    @Relationship("end-semester")
    private val endSemester: StudipSemester? = null

    @JsonIgnore
    override var semester: LectureSemesterInterface? = null
        get() {
            // Try to create semester instance if null
            if (field == null) {
                field = getLectureSemester()
            }

            return field
        }

    @JsonIgnore
    override val mainLanguage: String? = null

    @JsonProperty("description")
    override val lectureContent: String? = null

    @JsonIgnore
    override val duration: String? = null

    @JsonProperty("course-type")
    private val courseType: Int = 1

    @JsonIgnore
    override var lectureType: String? = null
        get() = getType()

    @JsonIgnore
    override val lecturers: List<String>? = null

    @Relationship("institute")
    private val instituteRelationship: InstituteRelationship? = null

    @JsonIgnore
    override var institute: String? = null
        get() = instituteRelationship?.name

    @JsonIgnore
    override val teachingMethod: String? = null

    @JsonIgnore
    override val teachingTargets: String? = null

    private fun getType(): String {
        return when (courseType) {
            1 -> "Vorlesung"
            2 -> "Seminar"
            3 -> "Übung"
            else -> "Sonstige"
        }
    }

    /**
     * Constructs a lecture semester
     */
    private fun getLectureSemester(): LectureSemesterInterface? {
        val semesterName = getFormattedSemester() ?: return null

        // Parse semester start date
        val startDate = if (startSemester?.start != null) {
            DateTime(startSemester.start)
        } else {
            null
        }

        return LectureSemester(semesterName, startDate)
    }

    /**
     * Builds a semester name from start semester and end semester
     */
    private fun getFormattedSemester(): String? {
        val hasStartSemester = startSemester != null && startSemester.title.isNotBlank()
        val hasEndSemester = endSemester != null && endSemester.title.isNotBlank()

        if (hasStartSemester && !hasEndSemester) {
            return startSemester!!.title
        }

        if (!hasStartSemester && hasEndSemester) {
            return endSemester!!.title
        }

        if (hasStartSemester && hasEndSemester) {
            if (startSemester!!.title != endSemester!!.title) {
                return "${startSemester!!.title} - ${endSemester!!.title}"
            } else {
                return startSemester!!.title
            }
        }

        return null
    }
}

