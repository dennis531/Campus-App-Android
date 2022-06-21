package de.tum.`in`.tumcampusapp.api.general.exception

class NotFoundException: ApiException() {
    override val message: String
        get() = "The requested resource can not be found"
}