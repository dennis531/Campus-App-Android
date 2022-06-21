package de.tum.`in`.tumcampusapp.component.tumui.lectures.di

import dagger.Subcomponent
import de.tum.`in`.tumcampusapp.component.tumui.lectures.fragment.LecturesFragment

@Subcomponent
interface LecturesComponent {
    fun inject(lecturesFragment: LecturesFragment)
}