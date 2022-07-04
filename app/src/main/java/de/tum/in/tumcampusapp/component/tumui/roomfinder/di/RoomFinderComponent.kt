package de.tum.`in`.tumcampusapp.component.tumui.roomfinder.di

import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.RoomFinderDetailsActivity

import dagger.Subcomponent
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.RoomFinderFragment

@Subcomponent()
interface RoomFinderComponent {
    fun inject(roomFinderFragment: RoomFinderFragment)
    fun inject(roomFinderDetailsActivity: RoomFinderDetailsActivity)
}
