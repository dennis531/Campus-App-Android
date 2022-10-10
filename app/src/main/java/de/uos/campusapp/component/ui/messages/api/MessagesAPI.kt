package de.uos.campusapp.component.ui.messages.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.messages.model.AbstractMessage
import de.uos.campusapp.component.ui.messages.model.AbstractMessageMember

/**
 * Api interface for the messages component
 */
interface MessagesAPI : BaseAPI {

    /**
     * Gets all messages from external system
     *
     * @return List of messages
     */
    fun getMessages(): List<AbstractMessage>

    /**
     * Send a new message in the external system
     *
     * @param message Message to be sent
     * @return Successfully sent message
     */
    fun sendMessage(message: AbstractMessage): AbstractMessage

    /**
     * Deletes message from external system
     *
     * @param message Message to be deleted
     */
    fun deleteMessage(message: AbstractMessage)

    /**
     * Searches a recipient in the external system
     *
     * @param query recipient search string
     * @return List of found recipients
     */
    fun searchRecipient(query: String): List<AbstractMessageMember>
}