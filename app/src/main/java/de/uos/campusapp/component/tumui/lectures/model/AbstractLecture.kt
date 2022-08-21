package de.uos.campusapp.component.tumui.lectures.model

import de.uos.campusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter

/**
 * Represents a lecture
 *
 * @property id
 * @property title Name of lecture
 * @property semester Semester name (optional)
 * @property mainLanguage Language (optional)
 * @property lectureContent Description of lecture (optional)
 * @property duration Duration, e.g. 12 SWS (optional)
 * @property lectureType Type, e.g. seminar (optional)
 * @property lecturers names of lectures (optional)
 * @property institute Related institute (optional)
 * @property teachingMethod teaching method, e.g. online (optional)
 * @property teachingTargets targets (optional)
 */
abstract class AbstractLecture : Comparable<AbstractLecture>, SimpleStickyListHeadersAdapter.SimpleStickyListItem {
    abstract val id: String
    abstract val title: String
    abstract val semester: String?
    abstract val mainLanguage: String?
    abstract val lectureContent: String?
    abstract val duration: String?
    abstract val lectureType: String?
    abstract val lecturers: List<String>?
    abstract val institute: String?
    abstract val teachingMethod: String?
    abstract val teachingTargets: String?

    override fun compareTo(other: AbstractLecture) = other.semester?.compareTo(semester ?: "") ?: 0

    override fun getHeadName() = semester ?: title

    override fun getHeaderId() = id

    companion object {
        @JvmField
        val Lecture_ID = "lecture_id"
    }
}