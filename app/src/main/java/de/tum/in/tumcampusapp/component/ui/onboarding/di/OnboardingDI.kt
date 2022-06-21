package de.tum.`in`.tumcampusapp.component.ui.onboarding.di

import dagger.*
import de.tum.`in`.tumcampusapp.component.ui.onboarding.*
import de.tum.`in`.tumcampusapp.component.ui.onboarding.legacy.CheckTokenFragment
import de.tum.`in`.tumcampusapp.component.ui.onboarding.legacy.OnboardingStartFragment
import javax.inject.Scope

@Scope
annotation class OnboardingScope

@OnboardingScope
@Subcomponent
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

interface OnboardingComponentProvider {
    fun onboardingComponent(): OnboardingComponent
}
