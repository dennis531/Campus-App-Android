package de.tum.`in`.tumcampusapp.component.tumui.calendar.model

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.notifications.model.FutureNotification
import de.tum.`in`.tumcampusapp.component.notifications.persistence.NotificationType
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.DateTimeUtils
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

abstract class AbstractEvent {
    abstract val id: String?
    abstract val title: String
    abstract val type: CalendarItemType?
    abstract val description: String?
    abstract val dtstart: DateTime?
    abstract val dtend: DateTime?
    abstract val location: String?
    abstract val isEditable: Boolean?

    val isFutureEvent: Boolean
        get() = dtstart?.isAfterNow ?: false

    private val tzGer = DateTimeZone.forID("Europe/Berlin")

    /**
     * Retrieve related values for calendar item as CalendarItem object
     */
    open fun toCalendarItem(): CalendarItem {
        return CalendarItem(
            id ?: "", title, type?.typeName ?: CalendarItem.LECTURE, description ?: "", getStartTimeInDeviceTimezone() ?: DateTime(),
            getEndTimeInDeviceTimezone() ?: DateTime(), location ?: "", isEditable ?: false
        )
    }

    fun toNotification(context: Context): FutureNotification? {
        val startTimeInDeviceTimeZone = getStartTimeInDeviceTimezone()
        val endTimeInDeviceTimeZone = getStartTimeInDeviceTimezone()

        if (id == null || startTimeInDeviceTimeZone == null || endTimeInDeviceTimeZone == null) {
            return null
        }

        val timestamp = DateTimeUtils.formatFutureTime(startTimeInDeviceTimeZone, context)
        val duration = endTimeInDeviceTimeZone.millis - startTimeInDeviceTimeZone.millis

        val notification = NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL_DEFAULT)
            .setContentTitle(title)
            .setContentText(timestamp)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_outline_event_24px)
            .setShowWhen(false)
            .setColor(ContextCompat.getColor(context, R.color.color_primary))
            .setTimeoutAfter(duration)
            .build()

        val notificationTime = startTimeInDeviceTimeZone.minusMinutes(15)
        return FutureNotification(NotificationType.CALENDAR, id!!.hashCode(), notification, notificationTime)
    }

    /**
     * If the device is in a different timezone than the german one, this method can be used to
     * map the event startTime to the user's timezone
     *
     * @return The event's startTime in the user's current timezone
     */
    private fun getStartTimeInDeviceTimezone(): DateTime? {
        return dtstart?.withZoneRetainFields(tzGer)?.withZone(DateTimeZone.getDefault())
    }

    /**
     * If the device is in a different timezone than the german one, this method can be used to
     * map the event endTime to the user's timezone
     *
     * @return The event's endTime in the user's current timezone
     */
    private fun getEndTimeInDeviceTimezone(): DateTime? {
        return dtend?.withZoneRetainFields(tzGer)?.withZone(DateTimeZone.getDefault())
    }
}