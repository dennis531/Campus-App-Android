package de.tum.`in`.tumcampusapp.component.ui.transportation

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.notifications.NotificationProvider
import de.tum.`in`.tumcampusapp.component.notifications.model.AppNotification
import de.tum.`in`.tumcampusapp.component.notifications.model.InstantNotification
import de.tum.`in`.tumcampusapp.component.notifications.persistence.NotificationType
import de.tum.`in`.tumcampusapp.component.other.locations.LocationManager
import de.tum.`in`.tumcampusapp.utils.Const

class TransportNotificationProvider(context: Context) : NotificationProvider(context) {

    override fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL_TRANSPORTATION)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(notificationColorAccent)
    }

    override fun buildNotification(): AppNotification? {
        val locationManager = LocationManager(context)
        val station = locationManager.getStation() ?: return null

        val title = context.getString(R.string.transport)
        val text = "Departures at ${station.name}"

        val inboxStyle = NotificationCompat.InboxStyle()
        TransportController
                .getDeparturesFromExternal(context, station)
                .blockingFirst()
                .map { "${it.means} (${it.direction}) in ${it.calculatedCountDown} min" }
                .forEach { inboxStyle.addLine(it) }

        val intent = station.getIntent(context)
        val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = getNotificationBuilder()
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(inboxStyle)
                .build()

        return InstantNotification(NotificationType.TRANSPORT, 0, notification)
    }
}
