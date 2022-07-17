package de.tum.`in`.tumcampusapp.component.ui.chat.api

import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatMember
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatMessage
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatRoom

interface ChatAPI {
    // Basic functions
    fun getChatRooms(): List<ChatRoom>
    fun getMessages(chatRoom: ChatRoom, latestMessage: ChatMessage?): List<ChatMessage> // get newest messages if latestMessage is null, otherwise messages older than the latest message
    fun sendMessage(chatRoom: ChatRoom, message: ChatMessage): ChatMessage

    // CHAT_ROOM_CREATEABLE
    fun createChatRoom(chatRoom: ChatRoom): ChatRoom? // The current user should be joined to the created chatroom

    // CHAT_ROOM_LEAVEABLE
    fun leaveChatRoom(chatRoom: ChatRoom) // The current user leaves the chat room

    // CHAT_ROOM_JOINABLE
    fun addMemberToChatRoom(chatRoom: ChatRoom, member: ChatMember): ChatRoom?

    // CHAT_ROOM_MEMBER_ADDABLE, Benötigt ebenfalls addMemberToChatRoom(..)
    fun searchChatMember(query: String): List<ChatMember>?

//    fun searchChatRoom(query: String): List<ChatRoom> // TODO: Needed for a search chat room function which could be implemented in the future
}