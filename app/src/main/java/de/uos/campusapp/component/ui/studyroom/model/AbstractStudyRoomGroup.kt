package de.uos.campusapp.component.ui.studyroom.model

/**
 * Represents a group of study room such as a library
 *
 * @property id
 * @property name Name of group, e.g. "Library Westerberg"
 * @property rooms List of study rooms
 */
abstract class AbstractStudyRoomGroup {
    abstract var id: String
    abstract var name: String
    abstract var rooms: List<AbstractStudyRoom>

    fun toStudyRoomGroup(): StudyRoomGroupItem {
        return StudyRoomGroupItem(id, name, rooms.map { it.toStudyRoomItem(this) })
    }
}