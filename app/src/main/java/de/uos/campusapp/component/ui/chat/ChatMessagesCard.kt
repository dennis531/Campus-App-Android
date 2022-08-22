package de.uos.campusapp.component.ui.chat

import android.content.Context
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import de.uos.campusapp.R
import de.uos.campusapp.component.other.navigation.NavDestination
import de.uos.campusapp.component.ui.chat.activity.ChatActivity
import de.uos.campusapp.component.ui.chat.model.ChatMessageItem
import de.uos.campusapp.component.ui.chat.model.ChatRoom
import de.uos.campusapp.component.ui.chat.model.ChatRoomDbRow
import de.uos.campusapp.component.ui.overview.CardInteractionListener
import de.uos.campusapp.component.ui.overview.CardManager.CARD_CHAT
import de.uos.campusapp.component.ui.overview.card.Card
import de.uos.campusapp.component.ui.overview.card.CardViewHolder
import de.uos.campusapp.database.TcaDb
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.Const
import java.util.ArrayList

/**
 * Card that shows the cafeteria menu
 */
class ChatMessagesCard(
    context: Context,
    room: ChatRoomDbRow
) : Card(CARD_CHAT, context, Component.CHAT, "card_chat") {

    private var mUnread: List<ChatMessageItem> = ArrayList()
    private var nrUnread = 0
    private var mRoomName = ""
    private var mRoomId = "0"

    private val chatMessageDao: ChatMessageDao

    override val optionsMenuResId: Int
        get() = R.menu.card_popup_menu

    init {
        val tcaDb = TcaDb.getInstance(context)
        chatMessageDao = tcaDb.chatMessageDao()
        setChatRoom(room.name, room.id)
    }

    override fun updateViewHolder(viewHolder: RecyclerView.ViewHolder) {
        super.updateViewHolder(viewHolder)
        val chatMessagesViewHolder = viewHolder as? ChatMessagesCardViewHolder
        chatMessagesViewHolder?.bind(mRoomName, mRoomId, mUnread)
    }

    /**
     * Sets the information needed to build the card
     *
     * @param roomName Name of the chat room
     * @param roomId Id of the chat room
     */
    private fun setChatRoom(roomName: String, roomId: String) {
        mRoomName = roomName
        chatMessageDao.deleteOldEntries()
        nrUnread = chatMessageDao.getNumberUnread(roomId)
        mUnread = chatMessageDao.getLastUnread(roomId).asReversed()
        mRoomId = roomId
    }

    override fun getNavigationDestination(): NavDestination? {
        val bundle = Bundle().apply {
            val chatRoom = ChatRoom(mRoomId, mRoomName)
            val value = Gson().toJson(chatRoom)
            putString(Const.CURRENT_CHAT_ROOM, value)
        }
        return NavDestination.Activity(ChatActivity::class.java, bundle)
    }

    override fun getId() = mRoomId.hashCode()

    override fun discard(editor: Editor) = chatMessageDao.markAsRead(mRoomId)

    companion object {

        @JvmStatic
        fun inflateViewHolder(
            parent: ViewGroup,
            interactionListener: CardInteractionListener
        ): CardViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_chat_messages, parent, false)
            return ChatMessagesCardViewHolder(view, interactionListener)
        }
    }
}
