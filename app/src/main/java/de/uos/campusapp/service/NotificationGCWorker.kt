package de.uos.campusapp.service

import android.content.Context
import androidx.work.*
import androidx.work.ListenableWorker.Result.success
import de.uos.campusapp.component.notifications.model.NotificationStore
import java.util.concurrent.TimeUnit

/**
 * Notification Garbage Collector which removes old notifications from database.
 */
class NotificationGCWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val persistentStore: NotificationStore by lazy {
        NotificationStore.getInstance(applicationContext)
    }

    override fun doWork(): Result {
        persistentStore.removeOld()
        return success()
    }

    companion object {

        fun getWorkRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
            return PeriodicWorkRequestBuilder<NotificationGCWorker>(7, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()
        }
    }
}