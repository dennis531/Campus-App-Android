package de.uos.campusapp.component.ui.messages

import android.content.Context
import de.uos.campusapp.api.general.CacheControl
import de.uos.campusapp.service.DownloadWorker
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import javax.inject.Inject

class MessagesDownloadAction @Inject constructor(
    private val context: Context,
    private val messagesController: MessagesController
) : DownloadWorker.Action {

    override fun execute(cacheBehaviour: CacheControl) {
        if (!ConfigUtils.isComponentEnabled(context, Component.MESSAGES)) {
            return
        }

        messagesController.fetchFromExternal()
    }
}
