package de.tum.`in`.tumcampusapp.component.ui.news.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import de.tum.`in`.tumcampusapp.component.ui.news.NewsDownloadAction
import de.tum.`in`.tumcampusapp.component.ui.news.NewsFragment
import de.tum.`in`.tumcampusapp.component.ui.news.api.NewsAPI
import de.tum.`in`.tumcampusapp.service.DownloadWorker
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils

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
