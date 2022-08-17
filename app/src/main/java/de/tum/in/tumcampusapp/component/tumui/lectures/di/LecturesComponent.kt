package de.tum.`in`.tumcampusapp.component.tumui.lectures.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import de.tum.`in`.tumcampusapp.component.tumui.lectures.api.LecturesAPI
import de.tum.`in`.tumcampusapp.component.tumui.lectures.fragment.LecturesFragment
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils

@Subcomponent(modules = [LecturesModule::class])
interface LecturesComponent {
    fun inject(lecturesFragment: LecturesFragment)
}

@Module
object LecturesModule {

    @JvmStatic
    @Provides
    fun provideLecturesClient(
        context: Context
    ): LecturesAPI = ConfigUtils.getApiClient(context, Component.LECTURES) as LecturesAPI
}
