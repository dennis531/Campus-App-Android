package de.uos.campusapp.component.ui.chat

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.uos.campusapp.component.ui.chat.model.ChatMessageItem
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom
import de.uos.campusapp.component.ui.chat.repository.ChatMessageLocalRepository
import de.uos.campusapp.component.ui.chat.repository.ChatMessageRemoteRepository
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChatMessageViewModel(
    private val localRepository: ChatMessageLocalRepository,
    private val remoteRepository: ChatMessageRemoteRepository
) : ViewModel() {

    fun markAsRead(room: String) = localRepository.markAsRead(room)

    fun deleteOldEntries() = localRepository.deleteOldEntries()

    fun addToUnsent(message: ChatMessageItem) = localRepository.addToUnsent(message)

    fun getAll(room: String): List<ChatMessageItem> = localRepository.getAllChatMessagesList(room)

    fun getNumberUnread(room: String): Int = localRepository.getNumberUnread(room)

    fun getUnsent(): List<ChatMessageItem> = localRepository.getUnsent()

    fun getUnsentInChatRoom(room: AbstractChatRoom): List<ChatMessageItem> {
        return localRepository.getUnsentInChatRoom(room.id)
    }

    fun getOlderMessages(
        room: AbstractChatRoom,
        message: ChatMessageItem
    ): Observable<List<ChatMessageItem>> {
        return remoteRepository
                .getMessages(room, message)
                .subscribeOn(Schedulers.io())
                .doOnNext { localRepository.replaceMessages(it) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getNewMessages(
        room: AbstractChatRoom
    ): Observable<List<ChatMessageItem>> {
        return remoteRepository
                .getNewMessages(room)
                .subscribeOn(Schedulers.io())
                .doOnNext { localRepository.replaceMessages(it) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun sendMessage(room: AbstractChatRoom, chatMessage: ChatMessageItem, context: Context): Disposable {
        val broadcastManager = LocalBroadcastManager.getInstance(context)

        return remoteRepository.sendMessage(room, chatMessage)
                .subscribeOn(Schedulers.io())
                .subscribe({ message ->
                    message.sendingStatus = ChatMessageItem.STATUS_SENT
                    localRepository.replaceMessage(message)
                    localRepository.removeUnsent(chatMessage)

                    // Send broadcast to eventually open ChatActivity
                    val intent = Intent(Const.CHAT_BROADCAST_NAME).apply {
                        putExtra(Const.CHAT_MESSAGE, message)
                    }
                    broadcastManager.sendBroadcast(intent)
                }, { t ->
                    Utils.logWithTag("ChatMessageViewModel", t.message ?: "unknown")
                    chatMessage.sendingStatus = ChatMessageItem.STATUS_ERROR
                    localRepository.replaceMessage(chatMessage)
                    val intent = Intent(Const.CHAT_BROADCAST_NAME)
                    broadcastManager.sendBroadcast(intent)
                })
    }
}