package de.uos.campusapp.component.ui.calendar.model

import android.content.Context
import android.text.format.DateUtils
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import de.uos.campusapp.R
import de.uos.campusapp.component.notifications.model.FutureNotification
import de.uos.campusapp.component.notifications.persistence.NotificationType
import de.uos.campusapp.utils.Const
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * Represents an event in the calendar
 *
 * @property id Identifier
 * @property title Title
 * @property type Type of event (Default: CalendarItemType.LECTURE)
 * @property description Description (optional)
 * @property dtstart Event start time (Default: DateTime())
 * @property dtend Event end time (Default: DateTime())
 * @property location Location (optional)
 * @property isEditable Can this event be changed or deleted? (Default: false)
 */
abstract class AbstractEvent {
    abstract val id: String?
    abstract val title: String
    abstract val type: CalendarEventType?
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

        val notificationTime = startTimeInDeviceTimeZone.minusMinutes(15)

        val timestamp = DateUtils.getRelativeTimeSpanString(
            startTimeInDeviceTimeZone.millis,
            notificationTime.millis,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString()
        val duration = endTimeInDeviceTimeZone.millis - startTimeInDeviceTimeZone.millis

        val notificationText = StringBuilder(timestamp)
        if (!location.isNullOrBlank()) {
            notificationText.append("\n")
            notificationText.append(location)
        }

        val notification = NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL_DEFAULT)
            .setContentTitle(title)
            .setContentText(notificationText)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_outline_event_24px)
            .setShowWhen(false)
            .setColor(ContextCompat.getColor(context, R.color.color_primary))
            .setTimeoutAfter(duration)
            .build()

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

enum class CalendarEventType(val typeName: String) {
    CANCELED(CalendarItem.CANCELED),
    LECTURE(CalendarItem.LECTURE),
    EXERCISE(CalendarItem.EXERCISE),
    OTHER(CalendarItem.OTHER);

    companion object {
        private val map = CalendarEventType.values().associateBy(CalendarEventType::typeName)
        fun fromType(typeName: String) = map[typeName] ?: CalendarEventType.OTHER
    }
}