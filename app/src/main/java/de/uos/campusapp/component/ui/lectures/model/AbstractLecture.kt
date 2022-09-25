package de.uos.campusapp.component.ui.lectures.model

import de.uos.campusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter

/**
 * Represents a lecture
 *
 * @property id
 * @property title Name of lecture
 * @property semester Semester (optional)
 * @property mainLanguage Language (optional)
 * @property lectureContent Description of lecture (optional)
 * @property duration Duration, e.g. 12 SWS (optional)
 * @property lectureType Type, e.g. seminar (optional)
 * @property lecturers names of lecturers (optional)
 * @property institute Related institute (optional)
 * @property teachingMethod teaching method, e.g. online (optional)
 * @property teachingTargets targets (optional)
 */
abstract class AbstractLecture : Comparable<AbstractLecture>, SimpleStickyListHeadersAdapter.SimpleStickyListItem {
    abstract val id: String
    abstract val title: String
    abstract val semester: LectureSemesterInterface?
    abstract val mainLanguage: String?
    abstract val lectureContent: String?
    abstract val duration: String?
    abstract val lectureType: String?
    abstract val lecturers: List<String>?
    abstract val institute: String?
    abstract val teachingMethod: String?
    abstract val teachingTargets: String?

    override fun compareTo(other: AbstractLecture): Int {
        return compareByDescending<AbstractLecture> { it.semester?.startDate }
            .thenByDescending { it.semester?.title }
            .thenBy { it.title }
            .compare(this, other)
    }

    override fun getHeadName() = semester?.title ?: title

    override fun getHeaderId() = semester?.title ?: title

    companion object {
        @JvmField
        val Lecture_ID = "lecture_id"
    }
}