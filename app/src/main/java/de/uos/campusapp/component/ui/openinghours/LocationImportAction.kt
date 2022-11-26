package de.uos.campusapp.component.ui.openinghours

import android.content.Context
import de.uos.campusapp.api.general.CacheControl
import de.uos.campusapp.component.ui.openinghours.api.OpeningHoursAPI
import de.uos.campusapp.database.CaDb
import de.uos.campusapp.service.DownloadWorker
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import java.io.IOException
import javax.inject.Inject

/**
 * Import default location and opening hours
 */
class LocationImportAction @Inject constructor(
    private val context: Context,
    private val database: CaDb
) : DownloadWorker.Action {

    private val apiClient: OpeningHoursAPI by lazy {
        ConfigUtils.getApiClient(context, Component.OPENINGHOUR) as OpeningHoursAPI
    }

    @Throws(IOException::class)
    override fun execute(cacheBehaviour: CacheControl) {
        if (!ConfigUtils.isComponentEnabled(context, Component.OPENINGHOUR)) {
            return
        }

        val openingHours = apiClient.getOpeningHours()
            .map { it.toLocationItem() }

        database.locationDao().removeCache()
        database.locationDao().replaceInto(openingHours)
    }
}
