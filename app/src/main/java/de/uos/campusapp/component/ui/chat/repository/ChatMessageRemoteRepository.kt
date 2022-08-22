package de.uos.campusapp.component.ui.chat.repository

import de.uos.campusapp.component.ui.chat.api.ChatAPI
import de.uos.campusapp.component.ui.chat.model.ChatMessageItem
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom
import io.reactivex.Observable

object ChatMessageRemoteRepository {

    lateinit var apiClient: ChatAPI

    fun getMessages(room: AbstractChatRoom, message: ChatMessageItem): Observable<List<ChatMessageItem>> =
            Observable.fromCallable {
                apiClient.getChatMessages(room, message.toChatMessage())
                    .map { it.toChatMessageItem() }
                    .toMutableList()
            }

    fun getNewMessages(room: AbstractChatRoom): Observable<List<ChatMessageItem>> =
            Observable.fromCallable {
                apiClient.getChatMessages(room, null)
                    .map { it.toChatMessageItem() }
                    .toMutableList()
            }

    fun sendMessage(room: AbstractChatRoom, message: ChatMessageItem): Observable<ChatMessageItem> =
            Observable.fromCallable {
                apiClient.sendChatMessage(room, message.toChatMessage()).toChatMessageItem()
            }
}