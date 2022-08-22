package de.uos.campusapp.component.ui.chat.repository

import de.uos.campusapp.component.ui.chat.model.ChatMessageItem
import de.uos.campusapp.database.TcaDb
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object ChatMessageLocalRepository {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    lateinit var db: TcaDb

    fun markAsRead(room: String) = db.chatMessageDao().markAsRead(room)

    fun deleteOldEntries() = db.chatMessageDao().deleteOldEntries()

    fun addToUnsent(message: ChatMessageItem) {
        executor.execute { db.chatMessageDao().replaceMessage(message) }
    }

    fun getAllChatMessagesList(room: String): List<ChatMessageItem> = db.chatMessageDao().getAll(room)

    fun getNumberUnread(room: String): Int = db.chatMessageDao().getNumberUnread(room)

    fun getUnsent(): List<ChatMessageItem> = db.chatMessageDao().unsent

    fun getUnsentInChatRoom(roomId: String): List<ChatMessageItem> = db.chatMessageDao().getUnsentInChatRoom(roomId)

    fun replaceMessages(chatMessages: List<ChatMessageItem>) {
        chatMessages.forEach { replaceMessage(it) }
    }

    fun replaceMessage(chatMessage: ChatMessageItem) {
        executor.execute { db.chatMessageDao().replaceMessage(chatMessage) }
    }

    fun removeUnsent(chatMessage: ChatMessageItem) {
        executor.execute { db.chatMessageDao().removeUnsent(chatMessage.text) }
    }
}