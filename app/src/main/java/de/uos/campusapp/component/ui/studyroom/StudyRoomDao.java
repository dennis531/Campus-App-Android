package de.uos.campusapp.component.ui.studyroom;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.uos.campusapp.component.ui.studyroom.model.StudyRoomItem;

@Dao
public interface StudyRoomDao {

    @Query("SELECT * FROM study_rooms WHERE group_id = :groupId")
    List<StudyRoomItem> getAll(String groupId);

    @Query("DELETE FROM study_rooms")
    void removeCache();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StudyRoomItem... studyRooms);

}
