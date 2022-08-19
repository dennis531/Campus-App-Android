package de.tum.`in`.tumcampusapp.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.Module
import dagger.Provides
import de.tum.`in`.tumcampusapp.api.auth.AuthManager
import de.tum.`in`.tumcampusapp.api.general.TUMCabeClient
import de.tum.`in`.tumcampusapp.api.tumonline.TUMOnlineClient
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import javax.inject.Singleton

/**
 * This module provides dependencies that are needed throughout the entire app, for instance the
 * database or the Retrofit clients.
 */
@Module
abstract class AppModule {

    @Module
    companion object {

        @JvmStatic
        @Singleton
        @Provides
        fun provideSharedPreferences(
            context: Context
        ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        @JvmStatic
        @Singleton
        @Provides
        fun provideTUMCabeClient( // TODO: Remove
            context: Context
        ): TUMCabeClient = TUMCabeClient.getInstance(context)

        @JvmStatic
        @Singleton
        @Provides
        fun provideTUMOnlineClient( // TODO: Remove
            context: Context
        ): TUMOnlineClient = TUMOnlineClient.getInstance(context)

        @JvmStatic
        @Singleton
        @Provides
        fun provideDatabase(
            context: Context
        ): TcaDb = TcaDb.getInstance(context)

        @JvmStatic
        @Singleton
        @Provides
        fun provideLocalBroadcastManager(
            context: Context
        ): LocalBroadcastManager = LocalBroadcastManager.getInstance(context)
    }
}
