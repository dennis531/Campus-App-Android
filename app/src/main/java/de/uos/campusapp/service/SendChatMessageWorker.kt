package de.uos.campusapp.service

import android.content.Context
import androidx.work.*
import androidx.work.ListenableWorker.Result.*
import androidx.work.NetworkType.CONNECTED
import de.uos.campusapp.api.auth.exception.AuthException
import de.uos.campusapp.component.ui.chat.ChatMessageViewModel
import de.uos.campusapp.component.ui.chat.ChatRoomController
import de.uos.campusapp.component.ui.chat.api.ChatAPI
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom
import de.uos.campusapp.component.ui.chat.repository.ChatMessageLocalRepository
import de.uos.campusapp.component.ui.chat.repository.ChatMessageRemoteRepository
import de.uos.campusapp.database.CaDb
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Utils
import java.util.concurrent.TimeUnit

/**
 * Service used to send chat messages.
 */
class SendChatMessageWorker(context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {

    private val tcaDb by lazy { CaDb.getInstance(applicationContext) }
    private val apiClient: ChatAPI by lazy { ConfigUtils.getApiClient(applicationContext, Component.CHAT) as ChatAPI }
    private val roomController: ChatRoomController by lazy { ChatRoomController(applicationContext) }

    override fun doWork(): ListenableWorker.Result {
        ChatMessageRemoteRepository.apiClient = apiClient
        ChatMessageLocalRepository.db = tcaDb

        val viewModel = ChatMessageViewModel(ChatMessageLocalRepository, ChatMessageRemoteRepository)
        viewModel.deleteOldEntries()

        return try {
            viewModel.getUnsent()
                    .asSequence()
                    .forEach {
                        val room = AbstractChatRoom.fromChatRoomDbRow(roomController.getRoomById(it.roomId))
                        viewModel.sendMessage(room, it, applicationContext)
                    }
            success()
        } catch (authException: AuthException) {
            // Retrying doesn't make any sense
            failure()
        } catch (e: Exception) {
            Utils.log(e)
            // Maybe the server is currently busy, but we really want to send the messages
            retry()
        }
    }

    companion object {
        @JvmStatic
        fun getWorkRequest(): WorkRequest {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(CONNECTED)
                    .build()
            return OneTimeWorkRequestBuilder<SendChatMessageWorker>()
                    .setConstraints(constraints)
                    .build()
        }

        fun getPeriodicWorkRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(CONNECTED)
                    .build()
            return PeriodicWorkRequestBuilder<SendChatMessageWorker>(3, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .build()
        }
    }
}