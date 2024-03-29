package de.uos.campusapp.api.general.exception

/**
 * Exception for resilience testing your network error handling
 * If this exception crashes your app, you should feel bad and implement proper error handling
 */
class ChaosMonkeyException(url: String) : ApiException("Some requests might spontaneously fail ($url)")