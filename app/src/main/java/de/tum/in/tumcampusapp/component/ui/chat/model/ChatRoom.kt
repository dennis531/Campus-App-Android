package de.tum.`in`.tumcampusapp.component.ui.chat.model

import de.tum.`in`.tumcampusapp.R.string.name

open class ChatRoom(
    open var id: String = "0",
    open var title: String = "",
    open var joined: Boolean = true
) {
    open var members: Int? = null

    val mode: Int
        get() = if (joined) MODE_JOINED else MODE_UNJOINED

    override fun toString() = "$id: $name"

    companion object {
        @JvmField
        val MODE_JOINED = 1
        @JvmField
        val MODE_UNJOINED = 0

        fun fromChatRoomDbRow(chatRoomDbRow: ChatRoomDbRow): ChatRoom {
            return ChatRoom(chatRoomDbRow.id, chatRoomDbRow.name, chatRoomDbRow.joined)
        }
    }
}
