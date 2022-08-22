package de.uos.campusapp.component.ui.chat.model

import de.uos.campusapp.R.string.name

/**
 * Represents a chat room
 *
 * @property id
 * @property title Name of chat room
 * @property joined Is current user joined?
 * @property members Number of joined members (optional)
 */
abstract class AbstractChatRoom {
    abstract var id: String
    abstract var title: String
    abstract var joined: Boolean
    abstract var members: Int?

    val mode: Int
        get() = if (joined) MODE_JOINED else MODE_UNJOINED

    override fun toString() = "$id: $name"

    companion object {
        @JvmField
        val MODE_JOINED = 1
        @JvmField
        val MODE_UNJOINED = 0

        fun fromChatRoomDbRow(chatRoomDbRow: ChatRoomDbRow): AbstractChatRoom {
            return ChatRoom(chatRoomDbRow.id, chatRoomDbRow.name, chatRoomDbRow.joined)
        }
    }
}
