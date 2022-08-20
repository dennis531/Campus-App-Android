package de.uos.campusapp.component.tumui.person.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import de.uos.campusapp.component.tumui.person.PersonDetailsFragment
import de.uos.campusapp.component.tumui.person.PersonSearchFragment
import de.uos.campusapp.component.tumui.person.api.PersonAPI
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils

@Subcomponent(modules = [PersonModule::class])
interface PersonComponent {
    fun inject(personSearchFragment: PersonSearchFragment)
    fun inject(personDetailsFragment: PersonDetailsFragment)
}

@Module
object PersonModule {

    @JvmStatic
    @Provides
    fun providePersonClient(
        context: Context
    ): PersonAPI = ConfigUtils.getApiClient(context, Component.PERSON) as PersonAPI
}