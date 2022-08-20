package de.uos.campusapp.component.ui.news.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import de.uos.campusapp.component.ui.news.NewsDownloadAction
import de.uos.campusapp.component.ui.news.NewsFragment
import de.uos.campusapp.component.ui.news.api.NewsAPI
import de.uos.campusapp.service.DownloadWorker
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils

@Subcomponent(modules = [NewsModule::class])
interface NewsComponent {
    fun inject(newsFragment: NewsFragment)
}

@Module
interface NewsModule {

    @Binds
    fun bindNewsDownloadAction(impl: NewsDownloadAction): DownloadWorker.Action

    companion object {
        @JvmStatic
        @Provides
        fun provideNewsClient(
            context: Context
        ): NewsAPI = ConfigUtils.getApiClient(context, Component.NEWS) as NewsAPI
    }
}
