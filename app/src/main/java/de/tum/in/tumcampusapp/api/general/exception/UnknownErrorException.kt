package de.tum.`in`.tumcampusapp.api.general.exception

class UnknownErrorException : ApiException() {

    override val message: String?
        get() = "Unknown Exception..."
}