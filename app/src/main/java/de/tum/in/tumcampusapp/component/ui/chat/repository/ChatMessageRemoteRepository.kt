package de.tum.`in`.tumcampusapp.component.ui.chat.repository

import de.tum.`in`.tumcampusapp.api.general.TUMCabeClient
import de.tum.`in`.tumcampusapp.api.general.model.TUMCabeVerification
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.component.ui.chat.api.ChatAPI
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatMessage
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatRoom
import io.reactivex.Observable

object ChatMessageRemoteRepository {

    lateinit var apiClient: ChatAPI

    fun getMessages(room: ChatRoom, message: ChatMessage): Observable<List<ChatMessage>> =
            Observable.fromCallable { apiClient.getMessages(room, message).toMutableList() }

    fun getNewMessages(room: ChatRoom): Observable<List<ChatMessage>> =
            Observable.fromCallable { apiClient.getMessages(room, null).toMutableList() }

    fun sendMessage(room: ChatRoom, message: ChatMessage): Observable<ChatMessage> =
            Observable.fromCallable { apiClient.sendMessage(room, message) }
}