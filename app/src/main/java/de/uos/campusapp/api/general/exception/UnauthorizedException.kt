package de.uos.campusapp.api.general.exception

class UnauthorizedException : ApiException() {

    override val message: String?
        get() = "The authentication failed or is not provided"
}