package de.uos.campusapp.component.notifications.model

import android.app.Notification
import de.uos.campusapp.component.notifications.persistence.NotificationType
import de.uos.campusapp.component.notifications.persistence.ScheduledNotification
import org.joda.time.DateTime

/**
 * Holds a [Notification] that is scheduled to be displayed instantly.
 *
 * @param id The identifier of the notification
 * @param notification The [Notification] that will be displayed to the user
 */
class InstantNotification(
    type: NotificationType,
    id: Int,
    notification: Notification
) : AppNotification(type, id, notification) {

    override fun toScheduledNotification(): ScheduledNotification {
        return ScheduledNotification(type.id, id, DateTime.now())
    }
}