package de.uos.campusapp.component.ui.lectures.model

/**
 * Simple implementation of [AbstractLecture]
 */
class Lecture(
    override val id: String,
    override val title: String,
    override val semester: LectureSemesterInterface? = null,
    override val mainLanguage: String? = null,
    override val lectureContent: String? = null,
    override val duration: String? = null,
    override val lectureType: String? = null,
    override val lecturers: List<String>? = null,
    override val institute: String? = null,
    override val teachingMethod: String? = null,
    override val teachingTargets: String? = null
) : AbstractLecture()