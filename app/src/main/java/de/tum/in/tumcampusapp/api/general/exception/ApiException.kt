package de.tum.`in`.tumcampusapp.api.general.exception

import java.lang.Exception

open class ApiException: Exception {
    constructor(): super()

    constructor(message: String): super(message)

    constructor(cause: Throwable): super(cause)

    constructor(message: String, cause: Throwable): super(message, cause)
}