package de.uos.campusapp.component.ui.studyroom.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import org.joda.time.DateTime

/**
 * Representation of a study room.
 */
@Entity(tableName = "study_rooms")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
open class StudyRoom(
    @PrimaryKey
    open var id: String,
    open var name: String,
    open var info: String? = "",
    @ColumnInfo(name = "group_id")
    open var studyRoomGroupId: String,
    @ColumnInfo(name = "occupied_until")
    open var occupiedUntil: DateTime? = null,
    @ColumnInfo(name = "free_until")
    open var freeUntil: DateTime? = null
) : Comparable<StudyRoom> {

    override fun compareTo(other: StudyRoom): Int {
        // We use the following sorting order:
        // 1. Rooms that are currently free and don't have a reservation coming up (freeUntil == null)
        // 2. Rooms that are currently free but have a reservation coming up (sorted descending by
        //    the amount of free time remaining)
        // 3. Rooms that are currently occupied but will be free soon (sorted ascending by the
        //    amount of occupied time remaining)
        // 4. The remaining rooms
        return compareBy<StudyRoom> { it.occupiedUntil }
                .thenBy { it.freeUntil?.millis?.times(-1) }
                .thenBy { it.name }
                .compare(this, other)
    }

    override fun toString() = name
}
