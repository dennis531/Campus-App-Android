package de.uos.campusapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import de.uos.campusapp.component.other.settings.SettingsFragment
import de.uos.campusapp.component.tumui.feedback.di.FeedbackComponent
import de.uos.campusapp.component.tumui.lectures.di.LecturesComponent
import de.uos.campusapp.component.tumui.person.di.PersonComponent
import de.uos.campusapp.component.tumui.roomfinder.di.RoomFinderComponent
import de.uos.campusapp.component.ui.cafeteria.di.CafeteriaComponent
import de.uos.campusapp.component.ui.messages.di.MessagesComponent
import de.uos.campusapp.component.ui.news.di.NewsComponent
import de.uos.campusapp.component.ui.onboarding.di.OnboardingComponent
import de.uos.campusapp.component.ui.overview.MainActivity
import de.uos.campusapp.component.ui.overview.MainFragment
import de.uos.campusapp.service.di.DownloadComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun cafeteriaComponent(): CafeteriaComponent
    fun downloadComponent(): DownloadComponent
    fun feedbackComponent(): FeedbackComponent.Builder
    fun lecturesComponent(): LecturesComponent
    fun messagesComponent(): MessagesComponent
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
