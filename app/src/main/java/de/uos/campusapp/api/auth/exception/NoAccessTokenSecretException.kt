package de.uos.campusapp.api.auth.exception

/**
 * No Access Token Secret could be found or has not been yet generated
 */
class NoAccessTokenSecretException(message: String = "") : AuthException(message)