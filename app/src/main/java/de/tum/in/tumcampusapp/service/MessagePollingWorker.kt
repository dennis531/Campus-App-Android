package de.tum.`in`.tumcampusapp.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.google.gson.Gson
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.auth.exception.AuthException
import de.tum.`in`.tumcampusapp.component.notifications.NotificationScheduler
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatMessageViewModel
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatNotificationProvider
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatRoomController
import de.tum.`in`.tumcampusapp.component.ui.chat.activity.ChatActivity
import de.tum.`in`.tumcampusapp.component.ui.chat.activity.ChatRoomsActivity
import de.tum.`in`.tumcampusapp.component.ui.chat.api.ChatAPI
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatMessage
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatRoom
import de.tum.`in`.tumcampusapp.component.ui.chat.repository.ChatMessageLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.chat.repository.ChatMessageRemoteRepository
import de.tum.`in`.tumcampusapp.component.ui.overview.MainActivity
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
import java.util.concurrent.TimeUnit

class MessagePollingWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val tcaDb by lazy { TcaDb.getInstance(applicationContext) }
    private val apiClient: ChatAPI by lazy { ConfigUtils.getLMSClient(applicationContext) as ChatAPI }
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
            return OneTimeWorkRequestBuilder<MessagePollingWorker>()
                .setConstraints(constraints)
                .build()
        }

        fun getPeriodicWorkRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            return PeriodicWorkRequestBuilder<MessagePollingWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        }
    }
}