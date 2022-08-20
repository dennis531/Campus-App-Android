package de.uos.campusapp.component.ui.cafeteria

import android.content.Context
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.component.ui.cafeteria.controller.CafeteriaMenuManager
import de.uos.campusapp.component.ui.cafeteria.repository.CafeteriaRemoteRepository
import de.uos.campusapp.service.DownloadWorker
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
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
