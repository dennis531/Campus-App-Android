package de.uos.campusapp.component.ui.chat.api

import de.uos.campusapp.component.ui.chat.model.AbstractChatMessage
import de.uos.campusapp.component.ui.chat.model.ChatMember
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom

interface ChatAPI {
    // Basic functions
    fun getChatRooms(): List<AbstractChatRoom>
    fun getChatMessages(chatRoom: AbstractChatRoom, latestMessage: AbstractChatMessage?): List<AbstractChatMessage> // get newest messages if latestMessage is null, otherwise messages older than the latest message
    fun sendChatMessage(chatRoom: AbstractChatRoom, message: AbstractChatMessage): AbstractChatMessage

    // CHAT_ROOM_CREATEABLE
    fun createChatRoom(chatRoom: AbstractChatRoom): AbstractChatRoom? // The current user should be joined to the created chatroom

    // CHAT_ROOM_LEAVEABLE
    fun leaveChatRoom(chatRoom: AbstractChatRoom) // The current user leaves the chat room

    // CHAT_ROOM_JOINABLE
    fun addMemberToChatRoom(chatRoom: AbstractChatRoom, member: ChatMember): AbstractChatRoom?

    // CHAT_ROOM_MEMBER_ADDABLE, Benötigt ebenfalls addMemberToChatRoom(..)
    fun searchChatMember(query: String): List<ChatMember>?

//    fun searchChatRoom(query: String): List<ChatRoom> // TODO: Needed for a search chat room function which could be implemented in the future
}