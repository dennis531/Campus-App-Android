package de.uos.campusapp.component.ui.chat.model

/**
 * Simple implementation of [AbstractChatRoom]
 */
data class ChatRoom(
    override var id: String,
    override var title: String,
    override var joined: Boolean = true,
    override var members: Int? = null
) : AbstractChatRoom()