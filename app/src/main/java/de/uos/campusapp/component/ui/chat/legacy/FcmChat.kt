package de.uos.campusapp.component.ui.chat.legacy

import java.io.Serializable

/**
 * Used for parsing the FCM json payload for the 'chat' type
 */
data class FcmChat(
    var room: String = "0",
    var member: String = "0",
    var message: String = "0"
) : Serializable {
    companion object {
        private const val serialVersionUID = -3920974316634829667L
    }
}
