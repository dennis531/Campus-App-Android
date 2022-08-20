package de.uos.campusapp.component.notifications.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import org.joda.time.DateTime

@Entity(tableName = "scheduled_notifications")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class ScheduledNotification(
    @ColumnInfo(name = "type_id")
    var typeId: Int = 0,
    @ColumnInfo(name = "content_id")
    var contentId: Int = 0,
    var date: DateTime = DateTime.now()
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
