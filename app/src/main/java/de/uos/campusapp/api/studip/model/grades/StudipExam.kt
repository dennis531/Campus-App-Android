package de.uos.campusapp.api.studip.model.grades

import de.uos.campusapp.component.tumui.grades.model.AbstractExam
import org.joda.time.DateTime

class StudipExam(
    override val id: String?,
    override val course: String,
    override val semester: String,
    override val date: DateTime? = null,
    override val examiner: String? = null,
    override val grade: Double? = null,
    override val type: String? = null,
    override val program: String? = null
) : AbstractExam() {
}