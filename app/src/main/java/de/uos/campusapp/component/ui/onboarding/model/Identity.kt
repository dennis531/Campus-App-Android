package de.uos.campusapp.component.ui.onboarding.model

/**
 * Simple identity implementation
 */
class Identity(
    override val id: String,
    override var username: String,
    override var fullName: String,
    override var email: String,
    override var imageUrl: String
) : IdentityInterface