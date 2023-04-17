package de.uos.campusapp.api.studip.interceptors

import android.content.Context
import de.uos.campusapp.api.general.exception.ForbiddenException
import de.uos.campusapp.api.general.exception.NotFoundException
import de.uos.campusapp.api.general.exception.UnauthorizedException
import de.uos.campusapp.api.general.exception.UnknownErrorException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.InterruptedIOException

class CheckErrorInterceptor(private val context: Context) : Interceptor {

    @Throws(InterruptedIOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Send the request to Stud.IP
        val response = chain.proceed(request)

        when (response.code) {
            401 -> throw UnauthorizedException()
            403 -> throw ForbiddenException()
            404 -> throw NotFoundException()
            409 -> throw UnknownErrorException()
            500 -> throw UnknownErrorException()
        }

        if (!response.isSuccessful) {
            throw UnknownErrorException()
        }

        // Because the request did not return an Error, we can re-enable LMS request
        // if they have been disabled before
//        Utils.setSetting(context, Const.TUMO_DISABLED, false)

        return response
    }
}