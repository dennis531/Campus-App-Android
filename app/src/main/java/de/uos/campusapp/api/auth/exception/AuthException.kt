package de.uos.campusapp.api.auth.exception

import java.lang.Exception

open class AuthException : Exception {
    constructor() : super()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)
}