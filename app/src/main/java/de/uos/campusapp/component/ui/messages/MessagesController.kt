package de.uos.campusapp.component.ui.messages

import android.annotation.SuppressLint
import android.content.Context
import de.uos.campusapp.api.general.CacheControl
import de.uos.campusapp.component.notifications.NotificationScheduler
import de.uos.campusapp.component.notifications.ProvidesNotifications
import de.uos.campusapp.component.ui.messages.model.AbstractMessage
import de.uos.campusapp.component.ui.messages.model.AbstractMessageMember
import de.uos.campusapp.component.ui.messages.model.MessageType
import de.uos.campusapp.component.ui.messages.repository.MessagesLocalRepository
import de.uos.campusapp.component.ui.messages.repository.MessagesRemoteRepository
import de.uos.campusapp.component.ui.overview.card.Card
import de.uos.campusapp.component.ui.overview.card.ProvidesCard
import de.uos.campusapp.database.CaDb
import de.uos.campusapp.utils.Utils
import org.joda.time.DateTime
import javax.inject.Inject

class MessagesController @Inject constructor(
    private val context: Context
) : ProvidesNotifications, ProvidesCard {

    private val localRepository: MessagesLocalRepository = MessagesLocalRepository(CaDb.getInstance(context))
    private val remoteRepository: MessagesRemoteRepository = MessagesRemoteRepository(context)

    /**
     * Downloads messages and stores them in the local repository synchronously.
     */
    @SuppressLint("CheckResult")
    fun fetchFromExternal() {
        try {
            val messages = getMessagesFromExternal()

            val lastMessage = getLastMessage()
            val lastMessageDate = lastMessage?.date ?: DateTime.now()

            // Flush all messages except outbox
            localRepository.clearCache()
            // Insert all messages
            localRepository.addMessages(messages)

            // Show notification
            showMessageNotification(messages, lastMessageDate)
        } catch (t: Throwable) {
            Utils.log(t)
        }
    }

    fun getMessagesFromExternal() = remoteRepository.getMessages()

    fun getAllMessagesFromDb() = localRepository.getAllMessages()

    fun getAllMessagesByType(messageType: MessageType) = localRepository.getAllByType(messageType)

    fun getLastMessage() = localRepository.getLastMessage()

    fun addMessage(message: AbstractMessage) = localRepository.addMessage(message)

    /**
     * Sends all outbox messages and stores the returned messages in the local repository
     *
     * @return All messages are sent successfully
     */
    fun sendMessages(): Boolean {
        // Get all outbox messages
        val outboxMessages = localRepository.getAllByType(MessageType.OUTBOX)

        // Send each message
        var success = true
        outboxMessages.forEach {
            try {
                val newMessage = remoteRepository.sendMessage(it)
                localRepository.deleteMessage(it)
                localRepository.addMessage(newMessage)
            } catch (t: Throwable) {
                Utils.log(t)
                success = false
            }
        }

        return success
    }

    /**
     * Deletes message in remote repository and local repository
     */
    fun deleteMessage(message: AbstractMessage) {
        if (message.type != MessageType.OUTBOX) {
            remoteRepository.deleteMessage(message)
        }

        // Delete message after successful remote deletion
        localRepository.deleteMessage(message)
    }

    fun searchRecipients(query: String): List<AbstractMessageMember> {
        return remoteRepository.searchRecipients(query)
    }

    private fun showMessageNotification(messages: List<AbstractMessage>, lastMessageDate: DateTime) {
        if (!hasNotificationsEnabled()) {
            return
        }

        val newMessages = messages.filter { it.type == MessageType.INBOX && it.date.isAfter(lastMessageDate) }
            .sortedByDescending { it.date }

        if (newMessages.isEmpty()) {
            return
        }

        val scheduler = NotificationScheduler(context)

        newMessages.forEach { newMessage ->
            val provider = MessagesNotificationProvider(context, newMessage)
            val notification = provider.buildNotification()

            scheduler.schedule(notification)
        }

        // Build summary for the notification group
        val summaryNotification = MessagesNotificationProvider.buildSummaryNotification(context, newMessages)
        scheduler.schedule(summaryNotification)
    }

    override fun hasNotificationsEnabled(): Boolean {
        return Utils.getSettingBool(context, "card_messages_phone", true)
    }

    override fun getCards(cacheControl: CacheControl): List<Card> {
        val results = ArrayList<Card>()

        // Get latest messages from database
        val messages = getAllMessagesByType(MessageType.INBOX)
        if (messages.isEmpty()) {
            return emptyList()
        }

        val card = MessagesCard(context, messages)
        card.getIfShowOnStart()?.let {
            results.add(it)
        }

        return results
    }
}