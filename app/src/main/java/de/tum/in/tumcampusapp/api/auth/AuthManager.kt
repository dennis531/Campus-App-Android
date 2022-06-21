package de.tum.`in`.tumcampusapp.api.auth

import okhttp3.Interceptor

abstract class AuthManager {
    abstract fun hasAccess(): Boolean
    abstract fun clear()

    abstract fun getInterceptor(): Interceptor
}