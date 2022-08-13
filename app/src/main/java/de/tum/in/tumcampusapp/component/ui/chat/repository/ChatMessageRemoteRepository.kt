package de.tum.`in`.tumcampusapp.component.ui.chat.repository

import de.tum.`in`.tumcampusapp.component.ui.chat.api.ChatAPI
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatMessage
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatRoom
import io.reactivex.Observable

object ChatMessageRemoteRepository {

    lateinit var apiClient: ChatAPI

    fun getMessages(room: ChatRoom, message: ChatMessage): Observable<List<ChatMessage>> =
            Observable.fromCallable { apiClient.getChatMessages(room, message).toMutableList() }

    fun getNewMessages(room: ChatRoom): Observable<List<ChatMessage>> =
            Observable.fromCallable { apiClient.getChatMessages(room, null).toMutableList() }

    fun sendMessage(room: ChatRoom, message: ChatMessage): Observable<ChatMessage> =
            Observable.fromCallable { apiClient.sendChatMessage(room, message) }
}