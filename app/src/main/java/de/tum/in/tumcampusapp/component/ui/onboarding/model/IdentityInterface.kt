package de.tum.`in`.tumcampusapp.component.ui.onboarding.model

/**
 * Represents the identity of the user. All fields are required.
 *
 * @property id identifier
 * @property username username (e.g. RZ ID)
 * @property fullName
 * @property email
 * @property imageUrl profile image url
 */
interface IdentityInterface {
    val id: String
    var username: String
    var fullName: String

    var email: String

    var imageUrl: String
}