package de.tum.`in`.tumcampusapp.component.tumui.person.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import de.tum.`in`.tumcampusapp.component.tumui.person.PersonDetailsFragment
import de.tum.`in`.tumcampusapp.component.tumui.person.PersonSearchFragment
import de.tum.`in`.tumcampusapp.component.tumui.person.api.PersonAPI
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils

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