package de.uos.campusapp.component.ui.cafeteria.di

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import de.uos.campusapp.component.ui.cafeteria.CafeteriaDownloadAction
import de.uos.campusapp.component.ui.cafeteria.details.CafeteriaDetailsSectionFragment
import de.uos.campusapp.component.ui.cafeteria.fragment.CafeteriaFragment
import de.uos.campusapp.service.DownloadWorker

@Subcomponent(modules = [CafeteriaModule::class])
interface CafeteriaComponent {
    fun inject(cafeteriaFragment: CafeteriaFragment)
    fun inject(cafeteriaDetailsSectionFragment: CafeteriaDetailsSectionFragment)
}

@Module
interface CafeteriaModule {

    @Binds
    fun bindCafeteriaDownloadAction(impl: CafeteriaDownloadAction): DownloadWorker.Action
}
