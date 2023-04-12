package de.uos.campusapp.component.ui.chat

import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import de.uos.campusapp.R
import de.uos.campusapp.component.notifications.NotificationProvider
import de.uos.campusapp.component.notifications.model.AppNotification
import de.uos.campusapp.component.notifications.model.InstantNotification
import de.uos.campusapp.component.notifications.persistence.NotificationType
import de.uos.campusapp.component.ui.chat.activity.ChatActivity
import de.uos.campusapp.component.ui.chat.activity.ChatRoomsActivity
import de.uos.campusapp.component.ui.chat.model.ChatMessageItem
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom
import de.uos.campusapp.component.ui.overview.CardManager
import de.uos.campusapp.component.ui.overview.MainActivity
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils

class ChatNotificationProvider(
    context: Context,
    private val chatRoom: AbstractChatRoom,
    private val newMessages: List<ChatMessageItem>
) : NotificationProvider(context) {

    private val sound: Uri = Uri.parse("android.resource://${context.packageName}/${R.raw.message}")

    override fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL_CHAT)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(Utils.getLargeIcon(context, R.drawable.ic_chat_with_lines))
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setLights(-0xffff01, 500, 500)
            .setSound(sound)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.color_primary))
    }

    override fun buildNotification(): AppNotification? {
        // Check if chat is currently open then don't show a notificationId if it is
        if (ChatActivity.mCurrentOpenChatRoom != null) {
            return null
        }

        if (!Utils.getSettingBool(context, "card_chat_phone", true)) {
            return null
        }

        val messagesText = newMessages.asReversed().map { it.text }
        val notificationText = messagesText.joinToString("\n")

        // Put the data into the intent
        val notificationIntent = Intent(context, ChatActivity::class.java).apply {
            putExtra(Const.CURRENT_CHAT_ROOM, Gson().toJson(chatRoom))
        }

        val taskStackBuilder = TaskStackBuilder.create(context).apply {
            addNextIntent(Intent(context, MainActivity::class.java))
            addNextIntent(Intent(context, ChatRoomsActivity::class.java))
            addNextIntent(notificationIntent)
        }

        val contentIntent = taskStackBuilder.getPendingIntent(0, FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notification = getNotificationBuilder()
            .setContentTitle(chatRoom.title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setContentText(notificationText)
            .setContentIntent(contentIntent)
            .build()

        val notificationId = (chatRoom.id.hashCode() shl 4) + NOTIFICATION_ID

        return InstantNotification(NotificationType.CHAT, notificationId, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = CardManager.CARD_CHAT
    }
}
