package de.tum.in.tumcampusapp.component.ui.chat;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatMessage;

@Dao
public interface ChatMessageDao {

    @Query("DELETE FROM chat_message WHERE timestamp<datetime('now','-1 month')")
    void deleteOldEntries();

    @Query("SELECT c.* FROM chat_message c WHERE c.roomId=:room ORDER BY c.sending, c.timestamp")
    List<ChatMessage> getAll(String room);

    @Query("UPDATE chat_room SET last_read = "
               + "(SELECT MAX(_id) FROM chat_message WHERE timestamp = "
                    + "(SELECT MAX(timestamp) FROM chat_message WHERE roomId=:room) "
               + "AND roomId=:room) "
           + "WHERE id=:room")
    void markAsRead(String room);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceMessage(ChatMessage m);

    @Query("DELETE FROM chat_message WHERE chat_message._id = '0' AND chat_message.text =:text")
    void removeUnsent(String text);

    @Query("DELETE FROM chat_message")
    void removeCache();

    @Query("SELECT c.* FROM chat_message c WHERE c.sending IN (1, 2) ORDER BY c.timestamp")
    List<ChatMessage> getUnsent();

    @Query("SELECT c.* FROM chat_message c WHERE c.roomId = :roomId AND c.sending IN (1, 2) ORDER BY c.timestamp")
    List<ChatMessage> getUnsentInChatRoom(String roomId);

    @Query("SELECT c.* FROM chat_message c, chat_room r "
           + "LEFT JOIN (SELECT _id, timestamp FROM chat_message) last ON (last._id=r.last_read) "
           + "WHERE c.roomId=:room AND c.roomId = r.id "
           + "AND CASE WHEN last.timestamp IS NOT NULL THEN last.timestamp < c.timestamp ELSE 1 END "
           + "ORDER BY c.timestamp DESC LIMIT 5")
    List<ChatMessage> getLastUnread(String room);

    @Query("SELECT count(*) "
           + "FROM chat_message c, chat_room r "
           + "LEFT JOIN (SELECT _id, timestamp FROM chat_message) last ON (last._id=r.last_read) "
           + "WHERE c.roomId=:room AND c.roomId = r.id "
           + "AND CASE WHEN last.timestamp IS NOT NULL THEN last.timestamp < c.timestamp ELSE 1 END")
    int getNumberUnread(String room);
}
