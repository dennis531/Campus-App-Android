package de.uos.campusapp.component.ui.onboarding.di

import android.content.Context
import dagger.*
import de.uos.campusapp.component.ui.onboarding.*
import de.uos.campusapp.component.ui.onboarding.api.OnboardingAPI
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import javax.inject.Scope

@Scope
annotation class OnboardingScope

@OnboardingScope
@Subcomponent(modules = [OnboardingModule::class])
interface OnboardingComponent {

    fun inject(onboardingActivity: OnboardingActivity)
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
