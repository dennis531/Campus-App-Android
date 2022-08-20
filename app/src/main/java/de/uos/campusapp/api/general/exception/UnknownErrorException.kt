package de.uos.campusapp.api.general.exception

class UnknownErrorException : ApiException() {

    override val message: String?
        get() = "Unknown Exception..."
}