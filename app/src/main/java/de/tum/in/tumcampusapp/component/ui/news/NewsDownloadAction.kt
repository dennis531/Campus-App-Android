package de.tum.`in`.tumcampusapp.component.ui.news

import android.content.Context
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.service.DownloadWorker
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
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
