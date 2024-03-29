package de.uos.campusapp.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.Module
import dagger.Provides
import de.uos.campusapp.database.CaDb
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
        fun provideDatabase(
            context: Context
        ): CaDb = CaDb.getInstance(context)

        @JvmStatic
        @Singleton
        @Provides
        fun provideLocalBroadcastManager(
            context: Context
        ): LocalBroadcastManager = LocalBroadcastManager.getInstance(context)
    }
}
