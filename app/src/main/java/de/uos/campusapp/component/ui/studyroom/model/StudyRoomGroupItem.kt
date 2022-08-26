package de.uos.campusapp.component.ui.studyroom.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Representation of a study room group
 */
@Entity(tableName = "study_room_groups")
open class StudyRoomGroupItem(
    @PrimaryKey
    open var id: String = "-1",
    open var name: String = "",
    @Ignore
    open var rooms: List<StudyRoomItem> = emptyList()
) : Comparable<StudyRoomGroupItem> {

    override fun toString() = name

    override fun compareTo(other: StudyRoomGroupItem) = name.compareTo(other.name)
}
