package de.uos.campusapp.component.ui.studyroom.model

import org.joda.time.DateTime

/**
 * Simple implementation of [AbstractStudyRoom]
 */
data class StudyRoom(
    override var id: String,
    override var name: String,
    override var info: String? = null,
    override var occupiedUntil: DateTime? = null,
    override var freeUntil: DateTime? = null
) : AbstractStudyRoom() {
}