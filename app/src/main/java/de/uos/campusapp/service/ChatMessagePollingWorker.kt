package de.uos.campusapp.service

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.*
import de.uos.campusapp.component.notifications.NotificationScheduler
import de.uos.campusapp.component.ui.chat.ChatMessageViewModel
import de.uos.campusapp.component.ui.chat.ChatNotificationProvider
import de.uos.campusapp.component.ui.chat.ChatRoomController
import de.uos.campusapp.component.ui.chat.api.ChatAPI
import de.uos.campusapp.component.ui.chat.model.ChatMessage
import de.uos.campusapp.component.ui.chat.model.ChatRoom
import de.uos.campusapp.component.ui.chat.repository.ChatMessageLocalRepository
import de.uos.campusapp.component.ui.chat.repository.ChatMessageRemoteRepository
import de.uos.campusapp.database.TcaDb
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Utils
import java.util.concurrent.TimeUnit

class ChatMessagePollingWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val tcaDb by lazy { TcaDb.getInstance(applicationContext) }
    private val apiClient: ChatAPI by lazy { ConfigUtils.getApiClient(applicationContext, Component.CHAT) as ChatAPI }
    private val roomController: ChatRoomController by lazy { ChatRoomController(applicationContext) }
    private val chatMessageDao by lazy { TcaDb.getInstance(applicationContext).chatMessageDao() }
    private val notificationScheduler by lazy { NotificationScheduler(applicationContext) }

    @SuppressLint("CheckResult")
    override fun doWork(): ListenableWorker.Result {
        ChatMessageRemoteRepository.apiClient = apiClient
        ChatMessageLocalRepository.db = tcaDb

        val viewModel = ChatMessageViewModel(ChatMessageLocalRepository, ChatMessageRemoteRepository)

        val rooms = roomController.getAllByStatus(ChatRoom.MODE_JOINED).map {
            ChatRoom.fromChatRoomDbRow(it.chatRoomDbRow!!)
        }

        // collect all new messages from each chat room
        for (room in rooms) {
            viewModel.getNewMessages(room).subscribe({onDataLoaded(room)}, {Utils.log(it)})
        }

        return Result.success()
    }

    private fun onDataLoaded(room: ChatRoom) {
        if (chatMessageDao.getNumberUnread(room.id) == 0) {
            return
        }

        val messages = chatMessageDao.getLastUnread(room.id)

        showNotification(room, messages)
    }

    private fun showNotification(room: ChatRoom, messages: List<ChatMessage>) {
        val provider = ChatNotificationProvider(applicationContext, room, messages)
        val notification = provider.buildNotification() ?: return
        notificationScheduler.schedule(notification)
    }

    companion object {
        @JvmStatic
        fun getWorkRequest(): WorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            return OneTimeWorkRequestBuilder<ChatMessagePollingWorker>()
                .setConstraints(constraints)
                .build()
        }

        fun getPeriodicWorkRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            return PeriodicWorkRequestBuilder<ChatMessagePollingWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        }
    }
}