package de.uos.campusapp.component.notifications.model

import android.content.Context
import de.uos.campusapp.component.notifications.persistence.ScheduledNotification
import de.uos.campusapp.database.TcaDb

class NotificationStore(context: Context) {

    private val dao = TcaDb.getInstance(context).scheduledNotificationsDao()

    fun find(notification: AppNotification): ScheduledNotification? {
        return dao.find(notification.type.id, notification.id)
    }

    fun update(id: Long, notification: AppNotification) {
        val scheduledNotification = notification.toScheduledNotification()
            .apply { this.id = id }
        return dao.update(scheduledNotification)
    }

    fun save(notification: AppNotification): Long {
        val scheduledNotification = notification.toScheduledNotification()
        return dao.insert(scheduledNotification)
    }

    fun remove(notification: AppNotification) {
        dao.delete(notification.type.id, notification.id)
    }

    fun removeOld() {
        dao.deleteOld()
    }

    companion object {

        private var INSTANCE: NotificationStore? = null

        @JvmStatic
        fun getInstance(context: Context): NotificationStore {
            if (INSTANCE == null) {
                INSTANCE = NotificationStore(context)
            }

            return INSTANCE!!
        }
    }
}