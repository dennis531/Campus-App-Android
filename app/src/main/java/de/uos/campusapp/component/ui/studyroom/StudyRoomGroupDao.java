package de.uos.campusapp.component.ui.studyroom;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.uos.campusapp.component.ui.studyroom.model.StudyRoomGroupItem;

@Dao
public interface StudyRoomGroupDao {

    @Query("SELECT * FROM study_room_groups")
    List<StudyRoomGroupItem> getAll();

    @Query("DELETE FROM study_room_groups")
    void removeCache();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StudyRoomGroupItem... studyRoomGroup);

}
