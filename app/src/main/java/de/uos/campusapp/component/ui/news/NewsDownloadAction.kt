package de.uos.campusapp.component.ui.news

import android.content.Context
import de.uos.campusapp.api.general.CacheControl
import de.uos.campusapp.service.DownloadWorker
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import javax.inject.Inject

class NewsDownloadAction @Inject constructor(
    private val context: Context,
    private val newsController: NewsController
) : DownloadWorker.Action {

    override fun execute(cacheBehaviour: CacheControl) {
        if (!ConfigUtils.isComponentEnabled(context, Component.NEWS)) {
            return
        }
        newsController.downloadFromExternal(cacheBehaviour)
    }
}
