package de.tum.`in`.tumcampusapp.service.di

import android.content.Context
import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import de.tum.`in`.tumcampusapp.api.general.TUMCabeClient
import de.tum.`in`.tumcampusapp.component.tumui.grades.GradesBackgroundUpdater
import de.tum.`in`.tumcampusapp.component.tumui.grades.GradesDownloadAction
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.CafeteriaDownloadAction
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.controller.CafeteriaMenuManager
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.repository.CafeteriaRemoteRepository
import de.tum.`in`.tumcampusapp.component.ui.news.NewsController
import de.tum.`in`.tumcampusapp.component.ui.news.NewsDownloadAction
import de.tum.`in`.tumcampusapp.component.ui.openinghour.LocationImportAction
import de.tum.`in`.tumcampusapp.component.ui.updatenote.UpdateNoteDownloadAction
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.service.DownloadWorker

/**
 * This module provides dependencies that are needed in the download functionality, namely
 * [DownloadWorker]. It mainly includes data repositories and manager classes.
 */
@Module
object DownloadModule {

    @JvmStatic
    @Provides
    fun provideAssetManager(
        context: Context
    ): AssetManager = context.assets

    @JvmStatic
    @Provides
    fun provideCafeteriaDownloadAction(
        context: Context,
        menuManager: CafeteriaMenuManager,
        remoteRepository: CafeteriaRemoteRepository
    ): CafeteriaDownloadAction = CafeteriaDownloadAction(context, menuManager, remoteRepository)

    @JvmStatic
    @Provides
    fun provideLocationImportAction(
        context: Context,
        database: TcaDb,
        tumCabeClient: TUMCabeClient
    ): LocationImportAction = LocationImportAction(context, database, tumCabeClient)

    @JvmStatic
    @Provides
    fun provideNewsDownloadAction(
        context: Context,
        newsController: NewsController
    ): NewsDownloadAction = NewsDownloadAction(context, newsController)

    @JvmStatic
    @Provides
    fun provideUpdateNoteDownloadAction(
        context: Context
    ): UpdateNoteDownloadAction = UpdateNoteDownloadAction(context)

    @JvmStatic
    @Provides
    fun provideGradesDownloadAction(
        context: Context,
        updater: GradesBackgroundUpdater
    ): GradesDownloadAction = GradesDownloadAction(context, updater)

    @JvmStatic
    @Provides
    fun provideWorkerActions(
        cafeteriaDownloadAction: CafeteriaDownloadAction,
        locationImportAction: LocationImportAction,
        gradesDownloadAction: GradesDownloadAction,
        newsDownloadAction: NewsDownloadAction,
        // updateNoteDownloadAction: UpdateNoteDownloadAction
    ): DownloadWorker.WorkerActions = DownloadWorker.WorkerActions(
            cafeteriaDownloadAction,
            locationImportAction,
            gradesDownloadAction,
            newsDownloadAction,
            // updateNoteDownloadAction
    )
}
