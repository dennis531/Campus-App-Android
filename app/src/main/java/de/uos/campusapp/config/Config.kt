package de.uos.campusapp.config

import androidx.annotation.Keep
import de.uos.campusapp.R
import de.uos.campusapp.component.other.locations.model.Campus
import de.uos.campusapp.component.ui.tuitionfees.model.Tuition
import de.uos.campusapp.component.ui.cafeteria.model.Cafeteria
import de.uos.campusapp.component.ui.transportation.model.Station
import de.uos.campusapp.utils.DateTimeUtils

/**
 * Api constants corresponding to an api client
 */
enum class Api {
    // Add more apis here
    STUDIP
}

/**
 * Transportation api constants
 */
enum class TransportationApi {
    // Add more transportation apis here
    MVV,
    VBN
}

/**
 * Cafeteria api constants
 */
enum class CafeteriaApi {
    // Add more cafeteria apis here
    MUNICH,
    OSNABRUECK
}

/**
 * Authentication method constants
 */
enum class AuthMethod {
    // Add more methods here
    OAUTH10A
}

/**
 * Signature method for network messages authenticated by OAuth 1.0a
 */
enum class OAuthSignatureMethod {
    OAUTH_PLAINTEXT,
    OAUTH_HMAC_SHA1,
    OAUTH_HMAC_SHA256
}

/**
 * Configuration object containing all set configuration options
 *
 * Constants not allowed, e.g. `const val CAMPUS = ...`
 */
@Keep
object Config {

    /**
     * List of [Campus] instances representing a campus location. Each instance contains the name, coordinates, cafeterias, and stations.
     * This information is used to display location-dependent information such as the nearest station.
     * Campus ids can be set freely and must be unique.
     *
     * If no cafeterias are provided and cafeteria component is enabled, the nearest cafeteria will be used for
     * location based cafeteria recommendations.
     * Cafeteria id must match with the cafeteria id used in the cafeteria api.
     *
     * Station list only needed for cards of the transportation component to determine the next station.
     * Station id must match with the station id used in the transportation api.
     *
     * First station and first cafeteria in the lists are defaults.
     *
     * Default: empty list
     */
    val CAMPUS = listOf<Campus>(
        // Coordinates of "Schlossinnenhof"
        Campus("I", "Campus Innenstadt", 52.27158, 8.04426, listOf(
            Cafeteria("1", "Mensa Schlossgarten")
        ), listOf(
            Station("1:000009071781", "Universität/Osnabrück Halle"),
            Station("1:000009071700", "Neumarkt"),
            Station("1:000009071516", "Arndtplatz")
        )),
        // Coordinates of "Mensavorplatz"
        Campus("W", "Campus Westerberg", 52.28431, 8.02342, listOf(
            Cafeteria("0", "Mensa Westerberg")
        ), listOf(
            Station("1:000009071756", "Campus Westerberg"),
            Station("1:000009071624", "Botanischer Garten"),
            Station("1:000009071548", "Nelson-Mandela-Platz"),
            Station("1:000009071788", "Walkmühlenweg")
        ))
    )

    /**
     * Name of organisation
     *
     * The value must be a string resource. Default: R.string.organisation
     */
    val ORGANISATION_NAME = R.string.organisation

    /*************
     * API options
     *************/

    /**
     * Main api used by all components
     *
     * To set a different api for a component, create a variable with the following format "{ComponentName}_API", e.g. "LECTURES_API"
     *
     * The value must be a constant of [Api]. Default: `Api.STUDIP`
     */
    val API = Api.STUDIP

    /**
     * Cafeteria api
     *
     * The value must be a constant of [CafeteriaApi]. Default: `CafeteriaApi.MUNICH`
     */
    val CAFETERIA_API = CafeteriaApi.OSNABRUECK

    /**
     * Transportation api
     *
     * The value must be a constant of [TransportationApi]. Default: `TransportationApi.MVV`
     */
    val TRANSPORTATION_API = TransportationApi.VBN

    /*********************
     * Stud.IP api options
     *********************/

    /**
     * Stud.IP base api url
     *
     * The value must be a string. Default: ""
     */
    val STUDIP_API_BASE_URL = "http://localhost/studip/jsonapi.php/v1/"

    /**
     * Stud.IP base url
     *
     * The value must be a string. Default: ""
     */
    val STUDIP_BASE_URL = "http://localhost/studip/"

    /*************************
     * UOS Backend API options
     *************************/

    /**
     * UOS backend base api url
     *
     * The value must be a string. Default: ""
     */
    val UOS_BACKEND_API_BASE_URL = "http://localhost:8000/api/"

    /************************
     * Authentication options
     ************************/

    /**
     * Main authentication method used by all apis
     *
     * To set a different authentication method for an api, create a variable with the following format "{ApiName}_AUTH_METHOD", e.g. `STUDIP_AUTH_METHOD`
     *
     * The value must be a constant of [AuthMethod]. Default: `AuthMethod.OAUTH10A`
     */
    val AUTH_METHOD = AuthMethod.OAUTH10A

    /***************
     * OAUTH options
     ***************/

    /**
     * OAuth 1.0a consumer key
     *
     * The value must be string. Default: ""
     */
    val OAUTH_CONSUMER_KEY = "123456789"

    /**
     * OAuth 1.0a consumer secret
     *
     * The value must be string. Default: ""
     */
    val OAUTH_CONSUMER_SECRET = "987654321"

    /**
     * OAuth 1.0a request token url
     *
     * The value must be string. Default: ""
     */
    val OAUTH_REQUEST_TOKEN_URL = "http://localhost/studip/dispatch.php/api/oauth/request_token"

    /**
     * OAuth 1.0a access token url
     *
     * The value must be string. Default: ""
     */
    val OAUTH_ACCESS_TOKEN_URL = "http://localhost/studip/dispatch.php/api/oauth/access_token"

    /**
     * OAuth 1.0a authorize url
     *
     * Callback URL is "campus://campus-app/oauth/callback". Configuration in file "R.values.strings.xml" possible.
     *
     * The value must be string. Default: ""
     */
    val OAUTH_AUTHORIZE_URL = "http://localhost/studip/dispatch.php/api/oauth/authorize"

    /**
     * OAuth 1.0a signature method for authenticated messages
     *
     * The value must be a constant of [OAuthSignatureMethod]. Default: `OAuthSignatureMethod.OAUTH_HMAC_SHA1`
     */
    val OAUTH_SIGNATURE_METHOD = OAuthSignatureMethod.OAUTH_PLAINTEXT

    /*******************
     * Component options
     *******************/

    /**
     * Component visibility
     *
     * Components can be enabled by variables with the following format "{ComponentName}_ENABLED", e.g. `CALENDAR_ENABLED = true`.
     * By default components are disabled.
     *
     * Component requirements to api and authentication can be configured in the [Component] enum class
     */

    val CALENDAR_ENABLED = true
    val GRADES_ENABLED = false
    val LECTURES_ENABLED = true
    val PERSON_ENABLED = true
    val ROOMFINDER_ENABLED = false
    val TUITIONFEES_ENABLED = true
    val CAFETERIA_ENABLED = false
    val CHAT_ENABLED = true
    val EDUROAM_ENABLED = true
    val GEOFENCING_ENABLED = true
    val NEWS_ENABLED = true
    val OPENINGHOUR_ENABLED = false
    val TRANSPORTATION_ENABLED = false
    val STUDYROOM_ENABLED = false
    val MESSAGES_ENABLED = true

    /*******************
     * Cafeteria options
     *******************/

    /**
     * Cafeteria additives text
     *
     * The text must be resource string and can be edited in the file "strings.xml". Default: null
     */
    val CAFETERIA_INGREDIENTS_TEXT = R.string.cafeteria_ingredients_os

    /******************
     * Calendar options
     ******************/

    /**
     * Enables the creation and deletion of calendar events
     *
     * The client of the configured api must implement the required functions.
     *
     * The value must be a boolean. Default: false
     */
    val CALENDAR_EDITABLE = false

    /**************
     * Chat options
     **************/

    /**
     * Activates the creation of chat rooms
     *
     * The client of the configured api must implement the required functions.
     *
     * The value must be a boolean. Default: true
     */
    val CHAT_ROOM_CREATEABLE = false

    /**
     * Allows to join chat rooms
     *
     * The client of the configured api must implement the required functions.
     *
     * The value must be a boolean. Default: true
     */
    val CHAT_ROOM_JOINABLE = false

    /**
     * Allows to leave a chat room
     *
     * The client of the configured api must implement the required functions.
     *
     * The value must be a boolean. Default: true
     */
    val CHAT_ROOM_LEAVEABLE = false

    /**
     * Allows to add members to a chat room by other users
     *
     * The client of the configured api must implement the required functions.
     * This option requires [CHAT_ROOM_JOINABLE] to be enabled. Otherwise members could not join chat rooms.
     *
     * The value must be a boolean. Default: true
     */
    val CHAT_ROOM_MEMBER_ADDABLE = false

    /*****************
     * Eduroam options
     *****************/

    /**
     * List of eduroam identity domains
     *
     * Allows to check if the user's eduroam wifi setting is set for this campus.
     * If this option is set for this campus, several eduroam configuration checks are performed.
     *
     * The value must be a list of strings. Default: empty list
     */
    val EDUROAM_ID_DOMAINS = listOf<String>("uos.de", "uni-osnabrueck.de")

    /**
     * Eduroam radius domain
     *
     * Allows to check if the correct authentication server is set in the wifi settings.
     * It is required for the eduroam set up.
     *
     * This value must be a string. Default: ""
     */
    val EDUROAM_RADIUS_DOMAIN = "uni-osnabrueck.de"

    /**
     * List of eduroam anonymous identities
     *
     * the identities are used to check if the configured anonymous identity is correct.
     * The first identity is required for eduroam set up.
     *
     * The value must be a string. Default: empty list
     */
    val EDUROAM_ANONYMOUS_IDENTITIES = listOf<String>("eduroam@uos.de", "eduroam@uni-osnabrueck.de", "anonymous@uni-osnabrueck.de")

    /**
     * Geofencing options
     *
     * Geofencing disables background task when the user leaves the geographical region
     * The Coordinates define the center of a circular region
     */

    /**
     * Latitude of the circular region
     *
     * The value must be a double. Default: 0.0
     */
    val GEOFENCING_LATITUDE = 52.27158 // latitude of "Campus Innenstadt"

    /**
     * Longitude of the circular region
     *
     * The value must be a double. Default: 0.0
     */
    val GEOFENCING_LONGITUDE = 8.04426 // longitude of "Campus Innenstadt"

    /**
     * Radius of the circular region
     *
     * The value must be a float. Default: 0f
     */
    val GEOFENCING_RADIUS_IN_METER = 50 * 1000f // 50 Km

    /******************
     * Lectures options
     ******************/

    /**
     * Activates the lectures files function. This function allows to list and download lecture files.
     *
     * The client of the configured api must implement the required functions.
     *
     * The value must be a boolean. Default: false
     */
    val LECTURES_SHOW_FILES = true

    /**
     * Activates the lecture records menu item in lecture details
     *
     * The client of the configured api must implement the required functions.
     *
     * The value must be a boolean. Default: false
     */
    val LECTURES_SHOW_RECORDS = false

    /**********************
     * Tuition fees options
     **********************/

    /**
     * Indicates whether the fees should be loaded from the api
     *
     * If true and the tuition api function is implemented by the client of the configured api,
     * the tuition will be loaded from the selected api.
     * Otherwise, the value from [TUITIONFEES_TUITION] will be loaded if set.
     *
     * The value must be a boolean. Default: false
     */
    val TUITIONFEES_FROM_API = false

    /**
     * [Tuition] instance containing tuition fees information
     *
     * The value must be an instance of [Tuition]. Default: null
     */
    val TUITIONFEES_TUITION = Tuition(
        start = DateTimeUtils.getDate("2022-07-01"),
        deadline = DateTimeUtils.getDate("2022-07-31"),
        semester = "Wintersemester 2023/24",
        amount = 335.03
    )

    /**
     * A tuition fees url for more information
     *
     * The value must be a string. Default: null
     */
    val TUITIONFEES_LINK = "https://www.uni-osnabrueck.de/studium/organisatorisches/rueckmeldung/"
}