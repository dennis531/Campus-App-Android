package de.uos.campusapp.component.ui.openinghours;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import de.uos.campusapp.component.ui.openinghours.model.LocationItem;

@Dao
public interface LocationDao {

    @Query("SELECT hours FROM location WHERE id = :id")
    String getHoursById(String id);

    @Query("SELECT * FROM location WHERE id = :id")
    LocationItem getLocationByReferenceId(String id);

    @Query("SELECT category FROM location GROUP BY category ORDER BY category")
    List<String> getCategories();

    @Query("SELECT * FROM location WHERE category = :category ORDER BY name")
    List<LocationItem> getAllOfCategory(String category);

    @Query("SELECT NOT count(*) FROM location")
    boolean isEmpty();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceInto(List<LocationItem> location);

    @Query("DELETE FROM location")
    void removeCache();
}
