package de.uos.campusapp.component.ui.chat;

import android.content.Context;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import de.uos.campusapp.api.tumonline.CacheControl;
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom;
import de.uos.campusapp.component.ui.chat.model.ChatRoomAndLastMessage;
import de.uos.campusapp.component.ui.chat.model.ChatRoomDbRow;
import de.uos.campusapp.component.ui.overview.card.Card;
import de.uos.campusapp.component.ui.overview.card.ProvidesCard;
import de.uos.campusapp.database.CaDb;
import de.uos.campusapp.utils.Utils;

/**
 * Chat room controller providing functions to get the messages and the cards
 */
public class ChatRoomController implements ProvidesCard {

    private Context mContext;
    private final ChatRoomDao chatRoomDao;

    /**
     * Constructor, open/create database, create table if necessary
     *
     * @param context Context
     */
    @Inject
    public ChatRoomController(Context context) {
        mContext = context;
        CaDb db = CaDb.Companion.getInstance(context);
        chatRoomDao = db.chatRoomDao();
    }

    public ChatRoomDbRow getRoomById(String id) {
        return chatRoomDao.getRoomById(id);
    }

    /**
     * Gets all chat rooms that you have joined(1)/not joined(0) for the specified room.
     *
     * @param joined chat room 1=joined, 0=not joined/left chat room, -1=not joined
     * @return List of chat messages
     */
    public List<ChatRoomAndLastMessage> getAllByStatus(int joined) {
        if (joined == AbstractChatRoom.MODE_JOINED) {
            return chatRoomDao.getAllRoomsJoinedList();
        } else {
            return chatRoomDao.getAllRoomsNotJoinedList();
        }
    }

    /**
     * Saves the given chat rooms into database
     */
    public void replaceIntoRooms(Collection<AbstractChatRoom> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            Utils.log("No rooms passed, can't insert anything.");
            return;
        }

        for (AbstractChatRoom room : rooms) {
            ChatRoomDbRow existingRoom = chatRoomDao.getRoomById(room.getId());

            if (existingRoom == null) {
                ChatRoomDbRow chatRoom = new ChatRoomDbRow(room.getId(), room.getTitle(), room.getJoined(), room.getMembers(), null, null);
                chatRoomDao.replaceRoom(chatRoom);
            } else {
                chatRoomDao.updateRoom(room.getId(), room.getTitle(), room.getJoined(), room.getMembers());
            }
        }
    }

    public void join(AbstractChatRoom currentChatRoom) {
        chatRoomDao.updateJoinedRooms(currentChatRoom.getId(), currentChatRoom.getTitle());
    }

    public void leave(AbstractChatRoom currentChatRoom) {
        chatRoomDao.updateLeftRooms(currentChatRoom.getId(), currentChatRoom.getTitle());
    }

    @NotNull
    @Override
    public List<Card> getCards(@NonNull CacheControl cacheControl) {
        List<Card> results = new ArrayList<>();

        try {
            // Get all rooms that have unread messages
            List<ChatRoomDbRow> rooms = chatRoomDao.getUnreadRooms();
            if (!rooms.isEmpty()) {
                for (ChatRoomDbRow room : rooms) {
                    ChatMessagesCard card = new ChatMessagesCard(mContext, room);
                    results.add(card.getIfShowOnStart());
                }
            }

            return results;
        } catch (Throwable t) {
            Utils.log(t);
            return results;
        }
    }
}
