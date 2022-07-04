package de.tum.`in`.tumcampusapp.config

import androidx.annotation.Keep
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.studip.StudipClient

enum class Api {
    STUDIP
}

enum class AuthMethod {
    OAUTH10A
}

enum class OAuthSignatureMethod {
    OAUTH_PLAINTEXT,
    OAUTH_HMAC_SHA1,
    OAUTH_HMAC_SHA256
}

/**
 * Constants not allowed
 */
@Keep
object Config {
    // API
    val API = Api.STUDIP
    val API_BASE_URL = "http://192.168.0.250/studip/jsonapi.php/v1/"

    // Authentication
    val AUTH_METHOD = AuthMethod.OAUTH10A

    // OAUTH
    val OAUTH_CONSUMER_KEY = "20952562292527a0bfcc2bf4425bbc6b0626fbee5"
    val OAUTH_CONSUMER_SECRET = "140ffd7ed20cb5396fc15e44e3a3e426"
    val OAUTH_REQUEST_TOKEN_URL = "http://192.168.0.250/studip/dispatch.php/api/oauth/request_token"
    val OAUTH_ACCESS_TOKEN_URL = "http://192.168.0.250/studip/dispatch.php/api/oauth/access_token"
    val OAUTH_AUTHORIZE_URL = "http://192.168.0.250/studip/dispatch.php/api/oauth/authorize"
    // Callback URL is "campus://campus-app/oauth/callback". Configuration in R.values.strings.xml possible.

    val OAUTH_SIGNATURE_METHOD = OAuthSignatureMethod.OAUTH_PLAINTEXT

    // Components
    val PERSON_ENABLED = true
    val CALENDAR_ENABLED = true
    val LECTURES_ENABLED = true
    val NEWS_ENABLED = true
    val EDUROAM_ENABLED = true
    val GRADES_ENABLED = true
    val TRANSPORTATION_ENABLED = false
    val OPENINGHOUR_ENABLED = false
    val CAFETERIA_ENABLED = false
    val TUTIONFEES_ENABLED = false
    val ROOMFINDER_ENABLED = true

    // Calendar options
    val CALENDAR_EDITABLE = false

    // Eduroam options
    // Allows to check if the user's eduroam wifi setting is set for this campus. If settings are set for this campus, several checks are performed.
    val EDUROAM_DOMAINS = listOf<String>("uos.de", "uni-osnabrueck.de")
    // Allows to check if the correct authentication server is set in the wifi settings
    val EDUROAM_RADIUS_SERVER = "radius.uni-osnabrueck.de"
    // Allows to check if the anonymous identity is correct
    val EDUROAM_ANONYMOUS_IDENTITIES = listOf<String>("eduroam@uos.de", "eduroam@uni-osnabrueck.de", "anonymous@uni-osnabrueck.de")
}