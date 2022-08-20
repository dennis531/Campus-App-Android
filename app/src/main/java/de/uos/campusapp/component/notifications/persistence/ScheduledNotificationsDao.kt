package de.uos.campusapp.component.notifications.persistence

import androidx.room.*

@Dao
interface ScheduledNotificationsDao {

    @Query("SELECT * FROM scheduled_notifications WHERE type_id = :typeId AND content_id = :contentId LIMIT 1")
    fun find(typeId: Int, contentId: Int): ScheduledNotification?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification: ScheduledNotification): Long

    @Update
    fun update(notification: ScheduledNotification)

    @Query("DELETE FROM scheduled_notifications WHERE type_id = :typeId AND content_id = :contentId")
    fun delete(typeId: Int, contentId: Int)

    @Query("DELETE FROM scheduled_notifications WHERE date < datetime('now','-3 month')")
    fun deleteOld()
}