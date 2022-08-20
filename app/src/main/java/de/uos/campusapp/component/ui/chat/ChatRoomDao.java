package de.uos.campusapp.component.ui.chat;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import de.uos.campusapp.component.ui.chat.model.ChatRoomAndLastMessage;
import de.uos.campusapp.component.ui.chat.model.ChatRoomDbRow;

/**
 * Queries needed for the ChatRoomActivity/Controller.
 */
@Dao
public interface ChatRoomDao {

    @Query("SELECT r.*, m.timestamp, m.text, unread.count as nr_unread " +
           "FROM chat_room r " +
           "LEFT JOIN (SELECT count(*) as count, c.roomId "
                    + "FROM chat_message c, chat_room cr "
                    + "LEFT JOIN (SELECT _id, timestamp FROM chat_message) last ON (last._id=cr.last_read) "
                    + "WHERE c.roomId = cr.id "
                    + "AND CASE WHEN last.timestamp IS NOT NULL THEN last.timestamp < c.timestamp ELSE 1 END "
                    + "GROUP BY c.roomId) unread on (unread.roomId=r.id) " +
           "LEFT JOIN (SELECT MAX(timestamp) timestamp, text, roomId FROM chat_message GROUP BY roomId) m ON (m.roomId=r.id) " +
           "WHERE joined=1 " +
           "ORDER BY datetime(m.timestamp) DESC, r.name")
    List<ChatRoomAndLastMessage> getAllRoomsJoinedList();

    @Query("SELECT r.*, m.timestamp, m.text, 0 as nr_unread " +
           "FROM chat_room r " +
           "LEFT JOIN (SELECT MAX(timestamp) timestamp, text, roomId FROM chat_message GROUP BY roomId) m ON (m.roomId=r.id) " +
           "WHERE joined=0 " +
           "ORDER BY datetime(m.timestamp) DESC, r.name")
    List<ChatRoomAndLastMessage> getAllRoomsNotJoinedList();

    @Query("UPDATE chat_room SET name=:name, joined=:joined, members=:members WHERE id=:id")
    void updateRoom(String id, String name, boolean joined, Integer members);

    @Update
    void updateRoom(ChatRoomDbRow room);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceRoom(ChatRoomDbRow room);

    @Query("SELECT * FROM chat_room")
    List<ChatRoomDbRow> getAll();

    @Query("SELECT * FROM chat_room WHERE id=:id")
    ChatRoomDbRow getRoomById(String id);

    @Query("UPDATE chat_room SET joined=0 WHERE joined=1")
    void markAsNotJoined();

    @Query("UPDATE chat_room SET name=:name, joined=1, members=:members WHERE id=:id")
    void updateRoomToJoined(String id, int members, String name);

    @Query("UPDATE chat_room SET name=:name, joined=1 WHERE id=:id")
    void updateJoinedRooms(String id, String name);

    @Query("UPDATE chat_room SET name=:name, joined=0 WHERE id=:id")
    void updateLeftRooms(String id, String name);

    @Query("UPDATE chat_room SET members=:memberCount WHERE id=:roomId")
    void updateMemberCount(int memberCount, String roomId);

    @Query("SELECT r.* " +
           "FROM chat_room r " +
           "LEFT JOIN (SELECT _id, timestamp FROM chat_message) last ON (last._id=r.last_read) " +
           "WHERE last.timestamp < (SELECT MAX(timestamp) FROM chat_message m WHERE m.roomId = r.id)")
    List<ChatRoomDbRow> getUnreadRooms();

    @Query("DELETE FROM chat_room")
    void removeCache();
}
