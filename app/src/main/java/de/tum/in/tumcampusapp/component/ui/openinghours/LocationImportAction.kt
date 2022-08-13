package de.tum.`in`.tumcampusapp.component.ui.openinghours

import android.content.Context
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.component.ui.openinghours.api.OpeningHoursAPI
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.service.DownloadWorker
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import java.io.IOException
import javax.inject.Inject

/**
 * Import default location and opening hours
 */
class LocationImportAction @Inject constructor(
    private val context: Context,
    private val database: TcaDb,
    private val apiClient: LMSClient
) : DownloadWorker.Action {

    @Throws(IOException::class)
    override fun execute(cacheBehaviour: CacheControl) {
        if (!ConfigUtils.isComponentEnabled(context, Component.OPENINGHOUR)) {
            return
        }

        val openingHours = (apiClient as OpeningHoursAPI).getOpeningHours()
        database.locationDao().removeCache()
        database.locationDao().replaceInto(openingHours)
    }
}
