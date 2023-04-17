package de.uos.campusapp.api.auth.exception

/**
 * No Access Token could be found or has not been yet generated
 */
class NoAccessTokenException(message: String = "") : AuthException(message)