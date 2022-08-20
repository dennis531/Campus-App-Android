package de.uos.campusapp.component.notifications.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.uos.campusapp.component.notifications.NotificationScheduler
import de.uos.campusapp.component.notifications.persistence.NotificationType
import de.uos.campusapp.component.tumui.tuitionfees.TuitionFeesNotificationProvider
import de.uos.campusapp.component.ui.cafeteria.CafeteriaNotificationProvider
import de.uos.campusapp.component.ui.transportation.TransportNotificationProvider
import de.uos.campusapp.utils.Const
import org.jetbrains.anko.doAsync

class NotificationAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val typeId = intent.getLongExtra(Const.KEY_NOTIFICATION_TYPE_ID, 0)
        val type = NotificationType.fromId(typeId)

        val notificationProvider = when (type) {
            NotificationType.CAFETERIA -> CafeteriaNotificationProvider(context)
            NotificationType.TRANSPORT -> TransportNotificationProvider(context)
            NotificationType.TUITION_FEES -> TuitionFeesNotificationProvider(context)
            else -> return
        }

        doAsync {
            val notification = notificationProvider.buildNotification()
            notification?.let {
                NotificationScheduler(context).schedule(it)
            }
        }
    }
}
