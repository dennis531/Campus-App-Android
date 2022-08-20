package de.uos.campusapp.component.ui.messages

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import de.uos.campusapp.R
import de.uos.campusapp.component.notifications.NotificationProvider
import de.uos.campusapp.component.notifications.model.AppNotification
import de.uos.campusapp.component.notifications.model.InstantNotification
import de.uos.campusapp.component.notifications.persistence.NotificationType
import de.uos.campusapp.component.ui.messages.activity.MessagesActivity
import de.uos.campusapp.component.ui.messages.model.AbstractMessage
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import java.lang.StringBuilder

class MessagesNotificationProvider(
    context: Context,
    private val message: AbstractMessage
): NotificationProvider(context) {

    override fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setGroup(GROUP_KEY_MESSAGES)
            .setAutoCancel(true)
            .setColor(notificationColorAccent)
    }

    override fun buildNotification(): AppNotification {
        val title = message.sender?.name ?: context.getString(R.string.new_message)
        val text = Utils.stripHtml(message.text)

        val intent = message.getIntent(context)
        val pendingIntent = PendingIntent.getActivity(
            context, message.id.hashCode(), intent, 0)

        val notification = getNotificationBuilder()
            .setContentTitle(title)
            .setContentText(message.subject)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${message.subject}\n$text"))
            .setContentIntent(pendingIntent)
            .build()

        return InstantNotification(NotificationType.MESSAGES, message.id.hashCode(), notification)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL = Const.NOTIFICATION_CHANNEL_MESSAGES
        private const val GROUP_KEY_MESSAGES = "de.uos.campusapp.MESSAGES"
        private const val SUMMARY_ID = 0

        /**
         * Build a summary for the notification group
         */
        @JvmStatic
        fun buildSummaryNotification(context: Context, messages: List<AbstractMessage>): AppNotification {
            val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle(context.getString(R.string.new_messages_format_string, messages.size))

            messages.forEach { message ->
                val line = StringBuilder()
                message.sender?.let { line.append(it.name + " ") }
                line.append(message.subject)

                inboxStyle.addLine(line)
            }

            val intent = Intent(context, MessagesActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle(context.getString(R.string.messages))
                // set content text to support devices running API level < 24
                .setContentText(context.getString(R.string.new_messages_format_string, messages.size))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setSubText(context.getString(R.string.messages))
                // build summary info into InboxStyle template
                .setStyle(inboxStyle)
                .setGroup(GROUP_KEY_MESSAGES)
                .setGroupSummary(true)
                .build()

            return InstantNotification(NotificationType.MESSAGES, SUMMARY_ID, notification)
        }
    }

}
