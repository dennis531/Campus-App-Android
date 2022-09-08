package de.uos.campusapp.api.studip.model.lectures

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import de.uos.campusapp.api.studip.model.person.InstituteRelationship
import de.uos.campusapp.component.ui.lectures.model.AbstractLecture

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
    override var semester: String? = null
        get() = getFormattedSemester()

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

