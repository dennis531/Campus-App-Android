package de.tum.`in`.tumcampusapp.component.ui.onboarding.api

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.component.ui.onboarding.model.IdentityInterface

/**
 * Represents an api which provides authentication mechanisms
 */
interface OnboardingAPI : BaseAPI {
    fun getIdentity(): IdentityInterface
}