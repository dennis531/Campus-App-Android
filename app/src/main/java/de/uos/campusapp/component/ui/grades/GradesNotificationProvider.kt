package de.uos.campusapp.component.ui.grades

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.NotificationCompat
import de.uos.campusapp.R
import de.uos.campusapp.component.notifications.NotificationProvider
import de.uos.campusapp.component.notifications.model.AppNotification
import de.uos.campusapp.component.notifications.model.InstantNotification
import de.uos.campusapp.component.notifications.persistence.NotificationType
import de.uos.campusapp.utils.Const

class GradesNotificationProvider(
    context: Context,
    private val newGrades: List<String>
) : NotificationProvider(context) {

    override fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL_DEFAULT)
                .setSmallIcon(R.drawable.ic_notification)
                .setGroup(GROUP_KEY_GRADES)
                .setColor(notificationColorAccent)
    }

    override fun buildNotification(): AppNotification? {
        val title = context.getString(R.string.my_grades)
        val size = newGrades.size
        val formattedNewGrades = newGrades.joinToString()

        val text = context.resources.getQuantityString(
                R.plurals.new_grades_format_string, size, size, formattedNewGrades)

        val intent = GradesActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)

        val deleteIntent = GradeNotificationDeleteReceiver.newIntent(context, newGrades)
        val deletePendingIntent = PendingIntent.getBroadcast(
                context, DELETE_REQUEST_CODE, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = getNotificationBuilder()
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .build()

        // We can pass 0 as the notification ID because only one notification at a time
        // will be active
        return InstantNotification(NotificationType.GRADES, 0, notification)
    }

    companion object {
        private const val GROUP_KEY_GRADES = "de.uos.campusapp.GRADES" // TODO: Rename
        private const val DELETE_REQUEST_CODE = 0
    }
}
