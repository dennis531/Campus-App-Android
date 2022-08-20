package de.uos.campusapp.component.other.generic

import android.app.Notification
import android.content.Context
import de.uos.campusapp.R
import de.uos.campusapp.service.legacy.FcmReceiverService.Companion.PushNotificationType
import de.uos.campusapp.utils.Utils
import java.io.IOException

/**
 * A generic push notification received via our backend server
 * @param appContext application context
 * @param type the concrete type ID of the notification
 * @param confirmation if the notification needs to be confirmed to the backend
 */
abstract class PushNotification(
    protected val appContext: Context,
    @PushNotificationType
    protected val type: Int,
    protected val notificationId: Int,
    private val confirmation: Boolean
) {
    protected val defaultIcon = R.drawable.ic_notification

    /**
     * A unique identifier for the displayed notification
     */
    abstract val displayNotificationId: Int

    /**
     * The android notification to show on the device
     */
    abstract val notification: Notification?

    /**
     * Send a confirmation to the backend, if it was requested
     */
    @Throws(IOException::class)
    fun sendConfirmation() {
        // Legacy support: notificationId id is -1 when old gcm messages arrive
        if (!confirmation || notificationId == -1) {
            return
        }
        Utils.logVerbose("Confirmed notificationId $notificationId")
//        TUMCabeClient.getInstance(appContext)
//                .confirm(notificationId)
    }
}
