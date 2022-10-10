package de.uos.campusapp.component.ui.onboarding.api

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.onboarding.model.IdentityInterface

/**
 * Api interface for the onboarding component
 */
interface OnboardingAPI : BaseAPI {

    /**
     * Loads user identity from external system
     *
     * @return Identity information of user
     */
    fun getIdentity(): IdentityInterface
}