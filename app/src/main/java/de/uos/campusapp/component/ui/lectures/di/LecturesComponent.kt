package de.uos.campusapp.component.ui.lectures.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import de.uos.campusapp.component.ui.lectures.api.LecturesAPI
import de.uos.campusapp.component.ui.lectures.fragment.LecturesFragment
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils

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
