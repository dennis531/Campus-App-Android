package de.tum.`in`.tumcampusapp.component.ui.openinghour

import android.content.Context
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.general.TUMCabeClient
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
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
    private val tumCabeClient: TUMCabeClient
) : DownloadWorker.Action {

    @Throws(IOException::class)
    override fun execute(cacheBehaviour: CacheControl) {
        if (!ConfigUtils.isComponentEnabled(context, Component.OPENINGHOUR)) {
            return
        }

        val openingHours = tumCabeClient.fetchOpeningHours(context.getString(R.string.language))
        database.locationDao().removeCache()
        database.locationDao().replaceInto(openingHours)
    }
}
