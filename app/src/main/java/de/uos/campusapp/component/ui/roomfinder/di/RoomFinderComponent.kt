package de.uos.campusapp.component.ui.roomfinder.di

import android.content.Context
import dagger.Module
import dagger.Provides
import de.uos.campusapp.component.ui.roomfinder.RoomFinderDetailsActivity

import dagger.Subcomponent
import de.uos.campusapp.component.ui.roomfinder.RoomFinderFragment
import de.uos.campusapp.component.ui.roomfinder.api.RoomFinderAPI
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils

@Subcomponent(modules = [RoomFinderModule::class])
interface RoomFinderComponent {
    fun inject(roomFinderFragment: RoomFinderFragment)
    fun inject(roomFinderDetailsActivity: RoomFinderDetailsActivity)
}

@Module
object RoomFinderModule {

    @JvmStatic
    @Provides
    fun provideRoomFinderClient(
        context: Context
    ): RoomFinderAPI = ConfigUtils.getApiClient(context, Component.ROOMFINDER) as RoomFinderAPI
}
