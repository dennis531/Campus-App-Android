package de.uos.campusapp.component.tumui.grades.model

import org.joda.time.DateTime

/**
 * Simple implementation of [AbstractExam]
 */
class Exam(
    override val id: String,
    override val course: String,
    override val semester: String,
    override val date: DateTime? = null,
    override val examiner: String? = null,
    override val grade: Double? = null,
    override val type: String? = null,
    override val program: String? = null
) : AbstractExam()