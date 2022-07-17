package de.tum.`in`.tumcampusapp.component.ui.chat

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.tum.`in`.tumcampusapp.api.general.model.TUMCabeVerification
import de.tum.`in`.tumcampusapp.component.ui.chat.legacy.FcmChat
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatMessage
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatRoom
import de.tum.`in`.tumcampusapp.component.ui.chat.repository.ChatMessageLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.chat.repository.ChatMessageRemoteRepository
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
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

    fun addToUnsent(message: ChatMessage) = localRepository.addToUnsent(message)

    fun getAll(room: String): List<ChatMessage> = localRepository.getAllChatMessagesList(room)

    fun getNumberUnread(room: String): Int = localRepository.getNumberUnread(room)

    fun getUnsent(): List<ChatMessage> = localRepository.getUnsent()

    fun getUnsentInChatRoom(room: ChatRoom): List<ChatMessage> {
        return localRepository.getUnsentInChatRoom(room.id)
    }

    fun getOlderMessages(
        room: ChatRoom,
        message: ChatMessage,
    ): Observable<List<ChatMessage>> {
        return remoteRepository
                .getMessages(room, message)
                .subscribeOn(Schedulers.io())
                .doOnNext { localRepository.replaceMessages(it) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getNewMessages(
        room: ChatRoom,
    ): Observable<List<ChatMessage>> {
        return remoteRepository
                .getNewMessages(room)
                .subscribeOn(Schedulers.io())
                .doOnNext { localRepository.replaceMessages(it) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun sendMessage(room: ChatRoom, chatMessage: ChatMessage, context: Context): Disposable {
        val broadcastManager = LocalBroadcastManager.getInstance(context)

        return remoteRepository.sendMessage(room, chatMessage)
                .subscribeOn(Schedulers.io())
                .subscribe({ message ->
                    message.sendingStatus = ChatMessage.STATUS_SENT
                    localRepository.replaceMessage(message)
                    localRepository.removeUnsent(chatMessage)

                    // Send broadcast to eventually open ChatActivity
                    val intent = Intent(Const.CHAT_BROADCAST_NAME).apply {
                        putExtra(Const.CHAT_MESSAGE, message)
                    }
                    broadcastManager.sendBroadcast(intent)
                }, { t ->
                    Utils.logWithTag("ChatMessageViewModel", t.message ?: "unknown")
                    chatMessage.sendingStatus = ChatMessage.STATUS_ERROR
                    localRepository.replaceMessage(chatMessage)
                    val intent = Intent(Const.CHAT_BROADCAST_NAME)
                    broadcastManager.sendBroadcast(intent)
                })
    }
}