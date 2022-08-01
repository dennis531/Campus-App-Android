package de.tum.`in`.tumcampusapp.component.ui.studyroom.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Representation of a study room group
 */
@Entity(tableName = "study_room_groups")
open class StudyRoomGroup(
    @PrimaryKey
    open var id: String = "-1",
    open var name: String = "",
    @Ignore
    open var rooms: List<StudyRoom> = emptyList()
) : Comparable<StudyRoomGroup> {

    override fun toString() = name

    override fun compareTo(other: StudyRoomGroup) = name.compareTo(other.name)
}
