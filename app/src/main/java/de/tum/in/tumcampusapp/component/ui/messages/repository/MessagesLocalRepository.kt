package de.tum.`in`.tumcampusapp.component.ui.messages.repository

import de.tum.`in`.tumcampusapp.component.ui.messages.model.AbstractMessage
import de.tum.`in`.tumcampusapp.component.ui.messages.model.MessageType
import de.tum.`in`.tumcampusapp.database.TcaDb
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

class MessagesLocalRepository @Inject constructor(
    private val database: TcaDb
) {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    fun addMessage(message: AbstractMessage) = executor.execute {
        database.messageDao().insert(message.toMessageItem())
    }

    fun addMessages(messages: List<AbstractMessage>) = database.messageDao().insert(messages.map { it.toMessageItem() })

    fun getAllMessages() = database.messageDao().getAll().map { it.toMessage() }

    fun getAllByType(messageType: MessageType) = database.messageDao().getAllByType(messageType.id).map { it.toMessage() }

    fun getLastMessage() = database.messageDao().getLast()?.toMessage()

    fun deleteMessage(message: AbstractMessage) = executor.execute {
        database.messageDao().delete(message.id)
    }

    fun clearCache() = database.messageDao().removeCache(listOf(MessageType.OUTBOX.id))

    fun clearAll() = database.messageDao().removeCache()
}