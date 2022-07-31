package de.tum.`in`.tumcampusapp.config

import androidx.annotation.Keep
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.locations.model.Campus
import de.tum.`in`.tumcampusapp.component.tumui.tuitionfees.model.Tuition
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.model.Cafeteria
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.Station
import de.tum.`in`.tumcampusapp.utils.DateTimeUtils

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

enum class TransportationApi {  // https://www.vbn.de/service/entwicklerinfos/opendata-und-openservice
    MVV
}

enum class CafeteriaApi {
    MUNICH
}

/**
 * Constants not allowed
 */
@Keep
object Config {
    // Campus locations
    // First values are defaults. Campus Ids can be set freely and must be unique. Station list only needed for cards of the transportation
    // component to determine the next station. Station id must match with the station id used in the transportation api.
    // If no cafeterias are provided and cafeteria component is enabled, the nearest cafeteria will be used for location based cafeteria recommendation.
    val CAMPUS = listOf<Campus>(
        Campus(
            "G", R.string.campus_garching, 48.2648424, 11.6709511, listOf(
                Cafeteria("422", "Mensa Garching"),
                Cafeteria("527", "StuCafé Boltzmannstraße"),
                Cafeteria("524", "StuCafé Mensa Garching"),
            ), listOf(
                Station("1000460", "Garching-Forschungszentrum"),
                Station("1000095", "Technische Universität"),
            )
        ),
        Campus("H", R.string.campus_garching_hochbrueck, 48.249432, 11.633905),
        Campus(
            "W", R.string.campus_weihenstephan, 48.397990, 11.722727, listOf(
                Cafeteria("423", "Mensa Weihenstephan"),
                Cafeteria("526", "StuCafé Akademie"),
                Cafeteria("525", "StuCafé Mensa-WST"),
            )
        ),
        Campus(
            "C", R.string.campus_main, 48.149436, 11.567635, listOf(
                Cafeteria("421", "Mensa Arcisstraße")
            ), listOf(
                Station("1000120", "Theresienstraße"),
                Station("1000095", "Technische Universität"),
                Station("1000051", "Pinakotheken"),
            )
        ),
        Campus(
            "K", R.string.campus_klinikum_großhadern, 48.110847, 11.4703001, listOf(
                Cafeteria("414", "Mensaria Großhadern"),
                Cafeteria("412", "Mensa Martinsried"),
            )
        ),
        Campus(
            "I", R.string.campus_klinikum, 48.137, 11.601119, stations = listOf(
                Station("1000580", "Max-Weber-Platz"),
            )
        ),
        Campus(
            "L", R.string.campus_leopoldstrasse, 48.155916, 11.583095, listOf(
                Cafeteria("411", "Mensa Leopoldstraße")
            )
        ),
        Campus("S", R.string.campus_geschwister_scholl, 48.150244, 11.580665),
    )

    // API
    val API = Api.STUDIP
    val API_BASE_URL = "http://192.168.178.91/studip/jsonapi.php/v1/"

    // Authentication
    val AUTH_METHOD = AuthMethod.OAUTH10A

    // OAUTH
    val OAUTH_CONSUMER_KEY = "20952562292527a0bfcc2bf4425bbc6b0626fbee5"
    val OAUTH_CONSUMER_SECRET = "140ffd7ed20cb5396fc15e44e3a3e426"
    val OAUTH_REQUEST_TOKEN_URL = "http://192.168.178.91/studip/dispatch.php/api/oauth/request_token"
    val OAUTH_ACCESS_TOKEN_URL = "http://192.168.178.91/studip/dispatch.php/api/oauth/access_token"
    val OAUTH_AUTHORIZE_URL = "http://192.168.178.91/studip/dispatch.php/api/oauth/authorize"
    // Callback URL is "campus://campus-app/oauth/callback". Configuration in R.values.strings.xml possible.

    val OAUTH_SIGNATURE_METHOD = OAuthSignatureMethod.OAUTH_PLAINTEXT

    // Components
    // By default components are disabled
    val PERSON_ENABLED = true
    val CALENDAR_ENABLED = true
    val LECTURES_ENABLED = true
    val NEWS_ENABLED = true
    val EDUROAM_ENABLED = true
    val GRADES_ENABLED = true
    val TUITIONFEES_ENABLED = true
    val ROOMFINDER_ENABLED = true
    val CHAT_ENABLED = true
    val OPENINGHOUR_ENABLED = true
    val TRANSPORTATION_ENABLED = true
    val CAFETERIA_ENABLED = true

    // Calendar options
    val CALENDAR_EDITABLE = false

    // Eduroam options
    // Allows to check if the user's eduroam wifi setting is set for this campus. If settings are set for this campus, several checks are performed.
    val EDUROAM_DOMAINS = listOf<String>("uos.de", "uni-osnabrueck.de")

    // Allows to check if the correct authentication server is set in the wifi settings
    val EDUROAM_RADIUS_SERVER = "radius.uni-osnabrueck.de"

    // Allows to check if the anonymous identity is correct
    val EDUROAM_ANONYMOUS_IDENTITIES = listOf<String>("eduroam@uos.de", "eduroam@uni-osnabrueck.de", "anonymous@uni-osnabrueck.de")

    // Tuition fees options
    // if true and tuition api is provided, the tuition will be loaded from the selected api
    val TUITIONFEES_FROM_API = false
    val TUITIONFEES_TUITION = Tuition(
        start = DateTimeUtils.getDate("2022-07-01"),
        deadline = DateTimeUtils.getDate("2022-07-31"),
        semester = "Wintersemester 2022/23",
        amount = 321.03
    )
    val TUITIONFEES_LINK = "https://www.uni-osnabrueck.de/studium/organisatorisches/rueckmeldung/"

    // Chat options
    val CHAT_ROOM_CREATEABLE = false // Default: True
    val CHAT_ROOM_JOINABLE = false // Default: True
    val CHAT_ROOM_LEAVEABLE = false // Default: True
    val CHAT_ROOM_MEMBER_ADDABLE = false // Allows to add members to a chat room by other users; Default: True

    // Transportation options
    val TRANSPORTATION_API = TransportationApi.MVV // Default: MVV

    // Cafeteria options
    val CAFETERIA_API = CafeteriaApi.MUNICH
    val CAFETERIA_INGREDIENTS_TEXT = R.string.cafeteria_ingredients // Edit the ingredients text in strings.xml; default: null
}