package de.tum.`in`.tumcampusapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import de.tum.`in`.tumcampusapp.component.other.settings.SettingsFragment
import de.tum.`in`.tumcampusapp.component.tumui.feedback.di.FeedbackComponent
import de.tum.`in`.tumcampusapp.component.tumui.lectures.di.LecturesComponent
import de.tum.`in`.tumcampusapp.component.tumui.person.di.PersonComponent
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.di.RoomFinderComponent
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.di.CafeteriaComponent
import de.tum.`in`.tumcampusapp.component.ui.news.di.NewsComponent
import de.tum.`in`.tumcampusapp.component.ui.onboarding.di.OnboardingComponent
import de.tum.`in`.tumcampusapp.component.ui.overview.MainActivity
import de.tum.`in`.tumcampusapp.component.ui.overview.MainFragment
import de.tum.`in`.tumcampusapp.service.di.DownloadComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun cafeteriaComponent(): CafeteriaComponent
    fun downloadComponent(): DownloadComponent
    fun feedbackComponent(): FeedbackComponent.Builder
    fun lecturesComponent(): LecturesComponent
    fun newsComponent(): NewsComponent
    fun onboardingComponent(): OnboardingComponent.Factory
    fun personComponent(): PersonComponent
    fun roomFinderComponent(): RoomFinderComponent

    fun inject(mainActivity: MainActivity)
    fun inject(mainFragment: MainFragment)
    fun inject(settingsFragment: SettingsFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}
