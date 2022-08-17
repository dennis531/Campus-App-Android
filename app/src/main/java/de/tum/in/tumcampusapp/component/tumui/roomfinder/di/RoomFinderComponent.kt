package de.tum.`in`.tumcampusapp.component.tumui.roomfinder.di

import android.content.Context
import dagger.Module
import dagger.Provides
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.RoomFinderDetailsActivity

import dagger.Subcomponent
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.RoomFinderFragment
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.api.RoomFinderAPI
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils

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
