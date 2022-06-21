package de.tum.`in`.tumcampusapp.component.tumui.person.di

import dagger.Subcomponent
import de.tum.`in`.tumcampusapp.component.tumui.person.PersonDetailsFragment
import de.tum.`in`.tumcampusapp.component.tumui.person.PersonSearchFragment

@Subcomponent
interface PersonComponent {
    fun inject(personSearchFragment: PersonSearchFragment)
    fun inject(personDetailsFragment: PersonDetailsFragment)
}