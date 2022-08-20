package de.uos.campusapp.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.WorkManager
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Utils

/**
 * Receives on boot completed broadcast, sets alarm for next sync-try
 * and start BackgroundService if enabled in settingsPrefix
 */
class StartSyncReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        // Check intent if called from StartupActivity
        startBackground(context)

        startNotificationGCWorker()

        startChatWorker(context)

        startMessageWorker(context)

        // Also start the SilenceService. It checks if it is enabled, so we don't need to
        // SilenceService also needs accurate timings, so we can't use WorkManager
        SilenceService.enqueueWork(context, Intent())
    }

    companion object {
        private const val UNIQUE_BACKGROUND = "START_SYNC_BACKGROUND"
        private const val UNIQUE_NOTIFICATION_GC = "UNIQUE_NOTIFICATION_GC"
        private const val UNIQUE_SEND_CHAT_MESSAGE = "START_SYNC_SEND_CHAT_MESSAGE"
        private const val UNIQUE_POLLING_CHAT_MESSAGE = "START_SYNC_POLLING_CHAT_MESSAGE"
        private const val UNIQUE_SEND_MESSAGE = "START_SYNC_SEND_MESSAGE"

        /**
         * Start the periodic BackgroundWorker, ensuring only one task is ever running
         */
        @JvmStatic
        fun startBackground(context: Context) {
            if (!Utils.isBackgroundServicePermitted(context)) {
                return
            }
            WorkManager.getInstance()
                    .enqueueUniquePeriodicWork(UNIQUE_BACKGROUND, KEEP,
                            BackgroundWorker.getWorkRequest())
        }

        private fun startNotificationGCWorker() {
            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(UNIQUE_NOTIFICATION_GC, KEEP,
                    NotificationGCWorker.getWorkRequest())
        }

        fun startChatWorker(context: Context) {
            if (!ConfigUtils.isComponentEnabled(context, Component.CHAT)) {
                return
            }

            val manager = WorkManager.getInstance()

            manager.enqueueUniquePeriodicWork(
                UNIQUE_SEND_CHAT_MESSAGE, KEEP, SendChatMessageWorker.getPeriodicWorkRequest())

            manager.enqueueUniquePeriodicWork(
                UNIQUE_POLLING_CHAT_MESSAGE, KEEP, ChatMessagePollingWorker.getPeriodicWorkRequest())
        }

        private fun startMessageWorker(context: Context) {
            if (!ConfigUtils.isComponentEnabled(context, Component.MESSAGES)) {
                return
            }

            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(UNIQUE_SEND_MESSAGE, KEEP,
                    SendMessageWorker.getPeriodicWorkRequest())
        }

        /**
         * Cancels the periodic BackgroundWorker
         */
        @JvmStatic
        fun cancelBackground() {
            WorkManager.getInstance()
                    .cancelUniqueWork(UNIQUE_BACKGROUND)
        }
    }
}
