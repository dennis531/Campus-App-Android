package de.uos.campusapp.component.other.general;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import de.uos.campusapp.component.other.general.model.Recent;

@Dao
public interface RecentsDao {
    int STATIONS = 1;
    int ROOMS = 2;
    int PERSONS = 3;

    @Nullable
    @Query("SELECT * FROM recent WHERE type=:type")
    List<Recent> getAll(Integer type);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Recent recent);

    @Query("DELETE FROM recent")
    void removeCache();
}
