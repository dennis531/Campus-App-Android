package de.uos.campusapp.component.ui.chat.model

import org.joda.time.DateTime

/**
 * Simple implementation of [AbstractChatMessage]
 */
data class ChatMessage(
    override var id: String,
    override var roomId: String,
    override var text: String,
    override var member: ChatMember,
    override var timestamp: DateTime
) : AbstractChatMessage()