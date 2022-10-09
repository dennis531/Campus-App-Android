package de.uos.campusapp.api.auth

import android.content.Context
import android.net.Uri
import de.uos.campusapp.R
import de.uos.campusapp.api.auth.exception.AuthException
import de.uos.campusapp.api.auth.exception.NoAccessTokenException
import de.uos.campusapp.api.auth.exception.NoAccessTokenSecretException
import de.uos.campusapp.config.OAuthSignatureMethod
import de.uos.campusapp.utils.*
import oauth.signpost.signature.*
import okhttp3.Interceptor
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.OkHttpOAuthProvider
import se.akerfeldt.okhttp.signpost.SigningInterceptor
import javax.inject.Inject

/**
 * Authentication manager for the OAuth 1.0a standard
 */
class OAuthManager @Inject constructor(private val context: Context) : AuthManager() {
    private val callbackURLScheme = context.getString(R.string.oauth_callback_scheme)
    private val callbackURLHost = context.getString(R.string.oauth_callback_host)
    private val callbackURLPathPrefix = context.getString(R.string.oauth_callback_path_prefix)

    private val callbackURL = "$callbackURLScheme://$callbackURLHost$callbackURLPathPrefix"

    private fun getAccessToken(): String {
        val accessToken = Utils.getSetting(context, Const.ACCESS_TOKEN, "")
        if (accessToken.isEmpty()) {
            throw NoAccessTokenException()
        }
        return accessToken
    }

    private fun getAccessTokenSecret(): String {
        val accessTokenSecret = Utils.getSetting(context, Const.ACCESS_TOKEN_SECRET, "")
        if (accessTokenSecret.isEmpty()) {
            throw NoAccessTokenSecretException()
        }
        return accessTokenSecret
    }

    /**
     * Returns whether a valid access token already exists.
     *
     * @return Whether access token is set
     */
    override fun hasAccess(): Boolean {
        val accessToken = Utils.getSetting(context, Const.ACCESS_TOKEN, "")
        return !accessToken.isEmpty() && accessToken.length > 2
    }

    fun getAuthenticationUrl(): String {
        // Fetch request token
        val authenticationUrl = provider.retrieveRequestToken(consumer, callbackURL)
        Utils.log("OAuth-Url: $authenticationUrl")
        return authenticationUrl
    }

    fun isCallbackUrl(url: Uri): Boolean {
        return url.toString().startsWith(callbackURL)
    }

    fun retrieveAccessToken(url: Uri) {
        provider.retrieveAccessToken(consumer, url.getQueryParameter("oauth_verifier"))
        saveAccessToken(consumer.token, consumer.tokenSecret)

        Utils.log("Access-Token: ${consumer.token}")
        Utils.log("Access-Token-Secret: ${consumer.tokenSecret}")
    }

    private fun saveAccessToken(token: String, tokenSecret: String) {
        Utils.setSetting(context, Const.ACCESS_TOKEN, token)
        Utils.setSetting(context, Const.ACCESS_TOKEN_SECRET, tokenSecret)
    }

    private fun getConsumer(): OkHttpOAuthConsumer {
        try {
            // Ensure that tokens are set
            consumer.setTokenWithSecret(getAccessToken(), getAccessTokenSecret())
        } catch (e: AuthException) {
            Utils.log("No access tokens are set")
        }
        return consumer
    }

    override fun clear() {
        saveAccessToken("", "")
        consumer.setTokenWithSecret("", "")
    }

    override fun getInterceptor(): Interceptor {
        return SigningInterceptor(getConsumer())
    }

    companion object {
        private val consumer = OkHttpOAuthConsumer(
            ConfigUtils.getConfig(ConfigConst.OAUTH_CONSUMER_KEY, ""),
            ConfigUtils.getConfig(ConfigConst.OAUTH_CONSUMER_SECRET, "")
        )

        private val provider = OkHttpOAuthProvider(
            ConfigUtils.getConfig(ConfigConst.OAUTH_REQUEST_TOKEN_URL, ""),
            ConfigUtils.getConfig(ConfigConst.OAUTH_ACCESS_TOKEN_URL, ""),
            ConfigUtils.getConfig(ConfigConst.OAUTH_AUTHORIZE_URL, "")
        )

        init {
            // Set up message signer
            val signer = when (ConfigUtils.getConfig(ConfigConst.OAUTH_SIGNATURE_METHOD, OAuthSignatureMethod.OAUTH_PLAINTEXT)) {
                OAuthSignatureMethod.OAUTH_HMAC_SHA1 -> HmacSha1MessageSigner()
                OAuthSignatureMethod.OAUTH_HMAC_SHA256 -> HmacSha256MessageSigner()
                else -> PlainTextMessageSigner()
            }
            consumer.setMessageSigner(signer)
        }
    }
}
