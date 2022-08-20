package de.uos.campusapp.service

import android.content.Context
import androidx.work.*
import androidx.work.ListenableWorker.Result.*
import androidx.work.NetworkType.CONNECTED
import de.uos.campusapp.component.ui.messages.MessagesController
import java.util.concurrent.TimeUnit

/**
 * Service used to send outbox messages.
 */
class SendMessageWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val messagesController: MessagesController by lazy {
        MessagesController(applicationContext)
    }

    override fun doWork(): ListenableWorker.Result {
        val success = messagesController.sendMessages()

        return if (success) {
            success()
        } else {
            failure()
        }
    }

    companion object {
        @JvmStatic
        fun getWorkRequest(): WorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(CONNECTED)
                .build()
            return OneTimeWorkRequestBuilder<SendMessageWorker>()
                .setConstraints(constraints)
                .build()
        }

        fun getPeriodicWorkRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(CONNECTED)
                .build()
            return PeriodicWorkRequestBuilder<SendMessageWorker>(3, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
        }
    }
}