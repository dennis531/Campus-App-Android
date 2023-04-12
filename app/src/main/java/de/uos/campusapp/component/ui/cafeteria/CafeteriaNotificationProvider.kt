package de.uos.campusapp.component.ui.cafeteria

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import de.uos.campusapp.R
import de.uos.campusapp.component.notifications.NotificationProvider
import de.uos.campusapp.component.notifications.model.AppNotification
import de.uos.campusapp.component.notifications.model.InstantNotification
import de.uos.campusapp.component.notifications.persistence.NotificationType
import de.uos.campusapp.component.other.locations.LocationManager
import de.uos.campusapp.component.ui.cafeteria.controller.CafeteriaMenuManager
import de.uos.campusapp.component.ui.cafeteria.repository.CafeteriaLocalRepository
import de.uos.campusapp.database.CaDb
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.DateTimeUtils
import org.joda.time.DateTime

class CafeteriaNotificationProvider(context: Context) : NotificationProvider(context) {

    private val cafeteriaMenuManager = CafeteriaMenuManager(context)
    private val cafeteriaLocalRepository = CafeteriaLocalRepository(CaDb.getInstance(context))

    override fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL_CAFETERIA)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_cutlery)
                .setGroup(GROUP_KEY_CAFETERIA)
                .setGroupSummary(true)
                .setShowWhen(false)
                .setColor(notificationColorAccent)
    }

    override fun buildNotification(): AppNotification? {
        val cafeteriaId = LocationManager(context).getCafeteria()
        if (cafeteriaId == Const.NO_CAFETERIA_FOUND) {
            return null
        }

        val cafeteria = cafeteriaLocalRepository.getCafeteriaWithMenus(cafeteriaId)
        val menus = cafeteria.menus
        val intent = cafeteria.getIntent(context)

        val inboxStyle = NotificationCompat.InboxStyle()
        menus.forEach { inboxStyle.addLine(it.notificationTitle) }

        val title = context.getString(R.string.cafeteria)

        val favoriteDishes = cafeteriaMenuManager.getFavoriteDishesServed(cafeteriaId, DateTime.now())

        // If any of the user's favorite dishes are served, we include them in the notification
        // text. Otherwise, we simply put the day's date.
        val text = if (favoriteDishes.isNotEmpty()) {
            val dishes = favoriteDishes.joinToString(", ") { it.name }
            context.getString(R.string.including_format_string, dishes)
        } else {
            DateTimeUtils.getDateString(cafeteria.nextMenuDate)
        }

        val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val summaryNotification = getNotificationBuilder()
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(inboxStyle)
                .setContentIntent(pendingIntent)
                .build()

        // We can pass 0 as the notification ID because only one notification at a time
        // will be active
        return InstantNotification(NotificationType.CAFETERIA, cafeteria.id.hashCode(), summaryNotification)
    }

    companion object {
        private const val GROUP_KEY_CAFETERIA = "de.uos.campusapp.CAFETERIA" // TODO: Rename
    }
}
