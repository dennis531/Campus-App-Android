package de.uos.campusapp.component.ui.messages.repository

import android.content.Context
import de.uos.campusapp.component.ui.messages.api.MessagesAPI
import de.uos.campusapp.component.ui.messages.model.AbstractMessage
import de.uos.campusapp.component.ui.messages.model.MessageMember
import de.uos.campusapp.component.ui.messages.model.MessageType
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import javax.inject.Inject

class MessagesRemoteRepository @Inject constructor(
    context: Context
) {
    private val apiClient: MessagesAPI = ConfigUtils.getApiClient(context, Component.MESSAGES) as MessagesAPI

    fun getMessages(): List<AbstractMessage> {
        return apiClient.getMessages()
    }

    fun sendMessage(message: AbstractMessage): AbstractMessage {
        return apiClient.sendMessage(message).apply {
            type = MessageType.SENT
        }
    }

    fun deleteMessage(message: AbstractMessage) {
        apiClient.deleteMessage(message)
    }

    fun searchRecipients(query: String): List<MessageMember> {
        return apiClient.searchRecipient(query)
    }
}