package de.tum.`in`.tumcampusapp.component.ui.chat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.RoomWarnings

@Entity(tableName = "chat_room", primaryKeys = ["id"])
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class ChatRoomDbRow(
    var id: String = "",
    var name: String = "",
    var joined: Boolean = false,
    var members: Int? = null,
    @ColumnInfo(name = "last_read")
    var lastRead: String? = null
) {

//    companion object {
//        fun fromLecture(lecture: AbstractLecture): ChatRoomDbRow {
//            return ChatRoomDbRow(-1, lecture.title, lecture.semester!!,
//                    lecture.semester!!, -1, lecture.id.toInt(),
//                    lecture.lecturers?.joinToString() ?: "", 0, -1)
//        }
//    }
}