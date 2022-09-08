package de.uos.campusapp.component.ui.calendar

import androidx.room.*
import de.uos.campusapp.component.ui.calendar.model.WidgetsTimetableBlacklist

@Dao
interface WidgetsTimetableBlacklistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(widgetsTimetableBlacklist: WidgetsTimetableBlacklist)

    @Delete
    fun delete(widgetsTimetableBlacklist: WidgetsTimetableBlacklist)

    @Query("DELETE FROM widgets_timetable_blacklist")
    fun flush()
}