package de.tum.`in`.tumcampusapp.component.ui.messages.api

import de.tum.`in`.tumcampusapp.component.ui.messages.model.AbstractMessage
import de.tum.`in`.tumcampusapp.component.ui.messages.model.MessageMember

interface MessagesAPI {
    fun getMessages(): List<AbstractMessage>
    fun sendMessage(message: AbstractMessage): AbstractMessage
    fun deleteMessage(message: AbstractMessage)
    fun searchRecipient(query: String): List<MessageMember>
}