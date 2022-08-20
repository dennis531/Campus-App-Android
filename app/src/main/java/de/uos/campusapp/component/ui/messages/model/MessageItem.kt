package de.uos.campusapp.component.ui.messages.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import org.joda.time.DateTime

@Entity(
    tableName = "message",
    primaryKeys = ["id", "type_id"] // Same message can have multiple types
)
data class MessageItem(
    val id: String,
    val subject: String,
    val text: String,
    @ColumnInfo(name = "type_id")
    val typeId: Int,
    val sender: MessageMember?,
    val recipients: List<MessageMember>,
    val date: DateTime
) {
    val type: MessageType
        get() = MessageType.fromId(typeId)

    fun toMessage(): AbstractMessage {
        return Message(id, subject, text, type, sender, recipients, date)
    }
}
