package de.uos.campusapp.component.ui.chat.api

import de.uos.campusapp.component.ui.chat.model.AbstractChatMessage
import de.uos.campusapp.component.ui.chat.model.ChatMember
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom
import de.uos.campusapp.utils.ConfigConst

/**
 * Api interface for the chat component
 */
interface ChatAPI {
    // Basic functions

    /**
     * Gets all chat rooms from external system
     *
     * @return List of chat rooms
     */
    fun getChatRooms(): List<AbstractChatRoom>

    /**
     * Gets all chat messages of the given room older than [latestMessage]. If [latestMessage] is null,
     * this function should return the latest messages.
     *
     * @param chatRoom Chat room of the requested messages
     * @param latestMessage Latest message
     * @return List of messages
     */
    fun getChatMessages(chatRoom: AbstractChatRoom, latestMessage: AbstractChatMessage?): List<AbstractChatMessage> // get newest messages if latestMessage is null, otherwise messages older than the latest message

    /**
     * Sends a chat message in the passed chat room.
     *
     * @param chatRoom Chat room of the message
     * @param message Message to be sent
     *
     * @return Successfully sent message
     */
    fun sendChatMessage(chatRoom: AbstractChatRoom, message: AbstractChatMessage): AbstractChatMessage

    // Conditional functions

    /**
     * Creates a new chat room in the external system. The current user should be joined to the created chatroom.
     *
     * Only required if config option [ConfigConst.CHAT_ROOM_CREATEABLE] is set to [true] in config.
     * Otherwise simply return [null].
     *
     * @param chatRoom chat room to be created
     * @return Successfully created chat room
     */
    fun createChatRoom(chatRoom: AbstractChatRoom): AbstractChatRoom?

    /**
     * Removes the current user from the chat room.
     *
     * Only required if config option [ConfigConst.CHAT_ROOM_LEAVEABLE] is set to [true] in config.
     * Otherwise provide an empty implementation.
     *
     * @param chatRoom chat room to be left
     */
    fun leaveChatRoom(chatRoom: AbstractChatRoom)

    /**
     * Adds a chat member to the passed chat room.
     *
     * Only required if config option [ConfigConst.CHAT_ROOM_JOINABLE] is set to [true] in config.
     * Otherwise simply return [null].
     *
     * @param chatRoom Chat room to be joined
     * @param member Chat member to be added
     * @return Updated chat room
     */
    fun addMemberToChatRoom(chatRoom: AbstractChatRoom, member: ChatMember): AbstractChatRoom?

    /**
     * Searches a chat member in the external system
     *
     * Only required if config option [ConfigConst.CHAT_ROOM_MEMBER_ADDABLE] is set to [true] in config.
     * Otherwise simply return [null].
     *
     * @param query Member search string
     * @return List of found members
     */
    fun searchChatMember(query: String): List<ChatMember>?

//    fun searchChatRoom(query: String): List<ChatRoom> // TODO: Needed for a search chat room function which could be implemented in the future
}