package de.tum.`in`.tumcampusapp.component.ui.onboarding.api

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.component.tumui.person.model.PersonInterface

/**
 * Represents an api which provides authentication mechanisms
 */
interface OnboardingAPI : BaseAPI {
    fun getIdentity(): PersonInterface // TODO: Create simpler model
}