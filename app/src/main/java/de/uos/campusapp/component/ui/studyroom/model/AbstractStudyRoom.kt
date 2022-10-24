package de.uos.campusapp.component.ui.studyroom.model

import org.joda.time.DateTime

/**
 * Represents a study room
 *
 * @property id
 * @property name Room name
 * @property info Description (optional)
 * @property occupiedUntil occupied until datetime; room will be displayed as free if this value is null (optional)
 * @property freeUntil free until datetime (optional)
 */
abstract class AbstractStudyRoom {
    abstract var id: String
    abstract var name: String
    abstract var info: String?
    abstract var occupiedUntil: DateTime?
    abstract var freeUntil: DateTime?

    fun toStudyRoomItem(studyRoomGroup: AbstractStudyRoomGroup): StudyRoomItem {
        return StudyRoomItem(id, name, info, studyRoomGroup.id, occupiedUntil, freeUntil)
    }
}