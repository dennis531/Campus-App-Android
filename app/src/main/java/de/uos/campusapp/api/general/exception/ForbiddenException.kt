package de.uos.campusapp.api.general.exception

class ForbiddenException : ApiException() {

    override val message: String?
        get() = "The user has not the permission to request this resource"
}