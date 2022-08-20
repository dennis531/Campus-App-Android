package de.uos.campusapp.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ListenableWorker.Result.retry
import androidx.work.ListenableWorker.Result.success
import androidx.work.NetworkType.CONNECTED
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.api.tumonline.CacheControl.BYPASS_CACHE
import de.uos.campusapp.api.tumonline.CacheControl.USE_CACHE
import de.uos.campusapp.di.injector
import de.uos.campusapp.utils.CacheManager
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.NetUtils
import de.uos.campusapp.utils.Utils
import javax.inject.Inject

class DownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    @Inject
    lateinit var downloadActions: DownloadWorker.WorkerActions

    init {
        Utils.log("DownloadService service has started")
        injector.downloadComponent().inject(this)
    }

    override fun doWork(): Result {
        return try {
            download(inputData, this)
            Utils.setSetting(applicationContext, LAST_UPDATE, System.currentTimeMillis())
            success()
        } catch (e: Exception) {
            Utils.log(e)
            retry()
        }
    }

    /**
     * Download all external data and returns whether the download was successful
     *
     * @param behaviour BYPASS_CACHE to force download over normal sync period
     * @return if all downloads were successful
     */
    private fun downloadAll(behaviour: CacheControl) {
        downloadActions.actions.forEach { it.execute(behaviour) }
    }

    interface Action {
        fun execute(cacheBehaviour: CacheControl)
    }

    class WorkerActions(vararg val actions: Action)

    companion object {

        const val TAG = "DOWNLOAD_WORKER"

        private const val FORCE_DOWNLOAD = "FORCE_DOWNLOAD"
        private const val FILL_CACHE = "FILL_CACHE"
        private const val LAST_UPDATE = "LAST_UPDATE"

        @JvmOverloads
        @JvmStatic
        fun getWorkRequest(
            behaviour: CacheControl = USE_CACHE,
            fillCache: Boolean = false
        ): OneTimeWorkRequest {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(CONNECTED)
                    .build()
            val data = Data.Builder()
                    .putBoolean(FORCE_DOWNLOAD, behaviour == BYPASS_CACHE)
                    .putBoolean(FILL_CACHE, fillCache)
                    .build()
            return OneTimeWorkRequestBuilder<DownloadWorker>()
                    .addTag(TAG)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .build()
        }

        /**
         * Gets the time when BackgroundService was called last time
         *
         * @param context Context
         * @return time when BackgroundService was executed last time
         */
        @JvmStatic
        fun lastUpdate(context: Context) = Utils.getSettingLong(context, LAST_UPDATE, 0L)

        /**
         * Download the data for a specific intent
         * note, that only one concurrent download() is possible with a static synchronized method!
         */
        @Synchronized
        private fun download(data: Data, service: DownloadWorker) {
            val force = if (data.getBoolean(FORCE_DOWNLOAD, false)) BYPASS_CACHE else USE_CACHE

            val backgroundServicePermitted = Utils.isBackgroundServicePermitted(service.applicationContext)
            if (!NetUtils.isConnected(service.applicationContext) || !backgroundServicePermitted) {
                return
            }

            service.downloadAll(force)

            val isAuthenticated = ConfigUtils.getAuthManagers(service.applicationContext).all { it.hasAccess() }
            val shouldFillCache = data.getBoolean(FILL_CACHE, false)

            if (isAuthenticated && shouldFillCache) {
                val cacheManager = CacheManager(service.applicationContext)
                cacheManager.fillCache()
            }
        }
    }
}
