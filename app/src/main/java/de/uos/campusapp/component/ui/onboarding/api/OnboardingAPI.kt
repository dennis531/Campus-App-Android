package de.uos.campusapp.component.ui.onboarding.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.onboarding.model.IdentityInterface

/**
 * Represents an api which provides authentication mechanisms
 */
interface OnboardingAPI : BaseAPI {
    fun getIdentity(): IdentityInterface
}