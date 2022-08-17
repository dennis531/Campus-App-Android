package de.tum.`in`.tumcampusapp.component.ui.onboarding.di

import android.content.Context
import dagger.*
import de.tum.`in`.tumcampusapp.component.ui.onboarding.*
import de.tum.`in`.tumcampusapp.component.ui.onboarding.api.OnboardingAPI
import de.tum.`in`.tumcampusapp.component.ui.onboarding.legacy.CheckTokenFragment
import de.tum.`in`.tumcampusapp.component.ui.onboarding.legacy.OnboardingStartFragment
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import javax.inject.Scope

@Scope
annotation class OnboardingScope

@OnboardingScope
@Subcomponent(modules = [OnboardingModule::class])
interface OnboardingComponent {

    fun inject(onboardingActivity: OnboardingActivity)
    fun inject(startFragment: OnboardingStartFragment)
    fun inject(checkTokenFragment: CheckTokenFragment)
    fun inject(extrasFragment: OnboardingExtrasFragment)
    fun inject(oauthFragment: OnboardingOAuth10aFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance activity: OnboardingActivity
        ): OnboardingComponent
    }
}

@Module
object OnboardingModule {

    @JvmStatic
    @Provides
    fun provideOnboardingClient(
        context: Context
    ): OnboardingAPI = ConfigUtils.getApiClient(context, Component.ONBOARDING) as OnboardingAPI
}


interface OnboardingComponentProvider {
    fun onboardingComponent(): OnboardingComponent
}
