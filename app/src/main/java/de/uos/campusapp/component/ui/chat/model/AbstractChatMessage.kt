package de.uos.campusapp.component.ui.chat.model

import org.joda.time.DateTime

/**
 * Represents a chat message
 *
 * @property id
 * @property roomId Chat room id
 * @property text Message content
 * @property member Message creator
 * @property timestamp Message create date
 */
abstract class AbstractChatMessage {
    abstract var id: String
    abstract var roomId: String
    abstract var text: String
    abstract var member: ChatMember
    abstract var timestamp: DateTime

    fun toChatMessageItem(): ChatMessageItem {
        return ChatMessageItem(id, roomId, text, member, timestamp)
    }
}