package de.tum.`in`.tumcampusapp.component.tumui.lectures.model

import de.tum.`in`.tumcampusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter

abstract class AbstractLecture : Comparable<AbstractLecture>, SimpleStickyListHeadersAdapter.SimpleStickyListItem {
    abstract val id: String
    abstract val title: String
    abstract val semester: String?
    abstract val mainLanguage: String?
    abstract val lectureContent: String?
    abstract val duration: String? //SWS
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