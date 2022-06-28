package de.tum.`in`.tumcampusapp.component.tumui.grades

import android.content.Context
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.service.DownloadWorker
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import javax.inject.Inject

class GradesDownloadAction @Inject constructor(
    private val context: Context,
    private val updater: GradesBackgroundUpdater
) : DownloadWorker.Action {

    override fun execute(cacheBehaviour: CacheControl) {
        if (!ConfigUtils.isComponentEnabled(context, Component.GRADES)) {
            return
        }
        updater.fetchGradesAndNotifyIfNecessary()
    }
}
