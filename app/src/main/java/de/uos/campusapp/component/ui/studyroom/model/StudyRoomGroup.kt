package de.uos.campusapp.component.ui.studyroom.model

/**
 * Simple implementation of [AbstractStudyRoomGroup]
 */
data class StudyRoomGroup(
    override var id: String,
    override var name: String,
    override var rooms: List<AbstractStudyRoom>
) : AbstractStudyRoomGroup()