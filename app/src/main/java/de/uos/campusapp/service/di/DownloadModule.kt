package de.uos.campusapp.service.di

import android.content.Context
import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import de.uos.campusapp.component.ui.grades.GradesBackgroundUpdater
import de.uos.campusapp.component.ui.grades.GradesDownloadAction
import de.uos.campusapp.component.ui.cafeteria.CafeteriaDownloadAction
import de.uos.campusapp.component.ui.cafeteria.controller.CafeteriaMenuManager
import de.uos.campusapp.component.ui.cafeteria.repository.CafeteriaRemoteRepository
import de.uos.campusapp.component.ui.messages.MessagesController
import de.uos.campusapp.component.ui.messages.MessagesDownloadAction
import de.uos.campusapp.component.ui.news.NewsController
import de.uos.campusapp.component.ui.news.NewsDownloadAction
import de.uos.campusapp.component.ui.openinghours.LocationImportAction
import de.uos.campusapp.component.ui.legacy.updatenote.UpdateNoteDownloadAction
import de.uos.campusapp.database.TcaDb
import de.uos.campusapp.service.DownloadWorker

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
    ): LocationImportAction = LocationImportAction(context, database)

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
    fun provideMessagesDownloadAction(
        context: Context,
        messagesController: MessagesController
    ): MessagesDownloadAction = MessagesDownloadAction(context, messagesController)

    @JvmStatic
    @Provides
    fun provideWorkerActions(
        cafeteriaDownloadAction: CafeteriaDownloadAction,
        locationImportAction: LocationImportAction,
        gradesDownloadAction: GradesDownloadAction,
        newsDownloadAction: NewsDownloadAction,
        messagesDownloadAction: MessagesDownloadAction,
        // updateNoteDownloadAction: UpdateNoteDownloadAction
    ): DownloadWorker.WorkerActions = DownloadWorker.WorkerActions(
            cafeteriaDownloadAction,
            locationImportAction,
            gradesDownloadAction,
            newsDownloadAction,
            messagesDownloadAction,
            // updateNoteDownloadAction
    )
}
