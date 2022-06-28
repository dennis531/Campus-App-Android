package de.tum.`in`.tumcampusapp.component.ui.cafeteria

import android.content.Context
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.controller.CafeteriaMenuManager
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.repository.CafeteriaRemoteRepository
import de.tum.`in`.tumcampusapp.service.DownloadWorker
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import javax.inject.Inject

class CafeteriaDownloadAction @Inject constructor(
    private val context: Context,
    private val cafeteriaMenuManager: CafeteriaMenuManager,
    private val cafeteriaRemoteRepository: CafeteriaRemoteRepository
) : DownloadWorker.Action {

    override fun execute(cacheBehaviour: CacheControl) {
        if (!ConfigUtils.isComponentEnabled(context, Component.CAFETERIA)) {
            return
        }

        cafeteriaMenuManager.downloadMenus(cacheBehaviour)
        cafeteriaRemoteRepository.fetchCafeterias(cacheBehaviour == CacheControl.BYPASS_CACHE)
    }
}
