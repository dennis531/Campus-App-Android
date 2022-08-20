package de.uos.campusapp.api.tumonline.interceptors

import android.content.Context
import de.uos.campusapp.api.general.exception.UnauthorizedException
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response

class CheckTokenInterceptor(private val context: Context) : Interceptor {

    @Throws(UnauthorizedException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Check for special requests
        val path = request.url.encodedPath
        val isTokenRequest = path.contains("requestToken")
        val isTokenConfirmationCheck = path.contains("isTokenConfirmed")

        // TUMonline requests are disabled if a request previously threw an InvalidTokenException
        val isTumOnlineDisabled = Utils.getSettingBool(context, Const.TUMO_DISABLED, false)

        if (!isTokenRequest && !isTokenConfirmationCheck && isTumOnlineDisabled) {
            throw UnauthorizedException()
        }

        return chain.proceed(request)
    }
}