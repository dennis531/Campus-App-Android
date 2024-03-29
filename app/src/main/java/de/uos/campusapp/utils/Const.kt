package de.uos.campusapp.utils

/**
 * Contains different constants used by several classes. Allows a unified access.
 */
object Const {
    const val MENSA_FOR_FAVORITEDISH = "FavoriteDishCafeteriaID"
    const val ACCESS_TOKEN = "access_token"
    const val ACCESS_TOKEN_SECRET = "access_token_secret"
    const val BACKGROUND_MODE = "background_mode"
    const val CAFETERIA_ID = "cafeteriasId"
    const val CAFETERIA_DATE = "cafeteriaDate"
    const val CAFETERIAS = "cafeterias"
    const val CAFETERIA_BY_LOCATION_SETTINGS_ID = "-1"
    const val CALENDAR_WEEK_MODE = "calender_week_mode"
    const val EVENT_BOOKED_MODE = "event_booked_mode"
    const val DATABASE_NAME = "tca.db"
    const val DATE = "date"
    const val ERROR = "error"
    const val MESSAGE = "message"
    const val USERNAME = "username"
    const val NEWS = "news"
    const val EVENTS = "events"
    const val STUDY_ROOM_GROUP_ID = "study_room_group_id"
    const val ROLE = "card_role"
    const val SILENCE_ON = "silence_on"
    const val SILENCE_SERVICE = "silent_mode"
    const val SILENCE_SERVICE_MODE = "silent_mode_set_to"
    const val SILENCE_OLD_STATE = "silence_old_state"
    const val TITLE_EXTRA = "title"

    const val API_HOSTNAME = ""

    const val CURRENT_CHAT_ROOM = "current_chat_room"
    const val CHAT_ROOM_NAME = "chat_room_name"

    const val EMPLOYEE_MODE = "employee_mode"

    const val SYNC_CALENDAR = "sync_calendar"
    const val PREFERENCE_SCREEN = "preference_screen"
    const val P_TOKEN = "pToken"

    const val SYNC_CALENDAR_IMPORT = "calendar_import"
    const val GROUP_CHAT_ENABLED = "group_chat_enabled"
    const val BUG_REPORTS = "bug_reports"
    const val DEFAULT_CAMPUS = "card_default_campus"
    const val NO_DEFAULT_CAMPUS_ID = "X"
    const val CHAT_MEMBER = "chat_member"
    const val POSITION = "position"

    const val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"

    const val URL_ROOM_FINDER_API = "/Api/roomfinder/room/"
    const val URL_DEFAULT_MAP_IMAGE = "https://$API_HOSTNAME${URL_ROOM_FINDER_API}defaultMap/"
    const val URL_MAP_IMAGE = "https://$API_HOSTNAME${URL_ROOM_FINDER_API}map/"
    const val ROOM_ID = "room_id"

    const val TUITION_PAID = "tuition_paid"

    const val KEY_NOTIFICATION_ID = "notificationID"
    const val KEY_NOTIFICATION = "notification"

    const val NOTIFICATION_CHANNEL_DEFAULT = "general"
    const val NOTIFICATION_CHANNEL_CHAT = "chat"
    const val NOTIFICATION_CHANNEL_MESSAGES = "messages"
    const val NOTIFICATION_CHANNEL_EDUROAM = "eduroam"
    const val NOTIFICATION_CHANNEL_CAFETERIA = "cafeteria"
    const val NOTIFICATION_CHANNEL_TRANSPORTATION = "transportation"
    const val NOTIFICATION_CHANNEL_EMERGENCY = "emergency"

    const val SILENCE_SERVICE_JOB_ID = 1002
    const val SEND_WIFI_SERVICE_JOB_ID = 1003
    const val DOWNLOAD_SERVICE_JOB_ID = 1004
    const val FILL_CACHE_SERVICE_JOB_ID = 1005
    const val QUERY_LOCATIONS_SERVICE_JOB_ID = 1006

    const val FEEDBACK = "feedback"
    const val FEEDBACK_IMG_COMPRESSION_QUALITY = 50
    const val FEEDBACK_TOPIC_GENERAL = "general"
    const val FEEDBACK_TOPIC_APP = "tca"

    const val EXTRA_FOREIGN_CONFIGURATION_EXISTS = "CONFIGURED_BY_OTHER_APP"
    const val GEOFENCING_SERVICE_JOB_ID = 1006
    const val ADD_GEOFENCE_EXTRA = "AddGeofence"
    const val CAMPUS_GEOFENCE = "geofence_campus_id"

    const val CARD_POSITION_PREFERENCE_SUFFIX = "_card_position"
    const val DISCARD_SETTINGS_START = "discard_settings_start"
    const val DISCARD_SETTINGS_PHONE = "discard_settings_phone"
    const val CALENDAR_ID_PARAM = "calendar_id"
    const val CALENDAR_SHOWN_IN_CALENDAR_ACTIVITY_PARAM = "calendar_shown_in_calendar_activity"

    const val RAINBOW_MODE = "rainbow_enabled"
    const val REFRESH_CARDS = "refresh_cards"
    const val EDUROAM_SSID = "eduroam"
    const val EDUROAM_DOMAIN = "eduroam.mwn.de"
    const val LRZ = "lrz"

    const val CHAT_MESSAGE = "chatMessage"
    const val CHAT_BROADCAST_NAME = "chat-message-received"

    const val EVENT_EDIT = "pEdit"
    const val EVENT_TITLE = "pTitel"
    const val EVENT_COMMENT = "pAnmerkung"
    const val EVENT_START = "pVon"
    const val EVENT_END = "pBis"
    const val EVENT_NR = "pTerminNr"

    const val EVENT_TIME = "event_time"

    const val CALENDAR_MONTHS_BEFORE = 2
    const val CALENDAR_MONTHS_AFTER = 2

    const val PDF_TITLE = "pdfTitle"
    const val PDF_PATH = "pdfPath"

    const val MESSAGE_REPLY = "messageReply"
    const val MESSAGE_LAST_DATE = "messageLastDate"

    const val NEWS_ALERT_IMAGE = "newsAlertImageURL"
    const val NEWS_ALERT_LINK = "newsAlertLink"
    const val NEWS_ALERT_SHOW_UNTIL = "newsAlertShowUntil"

    const val CONTACTS_PERMISSION_REQUEST_CODE = 0

    const val PROFILE_PICTURE_URL = "profile_picture_url"
    const val PROFILE_EMAIL = "profile_email"
    const val PROFILE_ID = "profile_id"
    const val PROFILE_DISPLAY_NAME = "profile_display_name"

    const val CALENDAR_FILTER_CANCELED = "calendar_filter_canceled"
    const val CALENDAR_FILTER_HOUR_LIMIT_MIN = "calendar_filter_hour_limit_min"
    const val CALENDAR_FILTER_HOUR_LIMIT_MAX = "calendar_filter_hour_limit_max"
    const val CALENDAR_FILTER_HOUR_LIMIT_MIN_DEFAULT = "8"
    const val CALENDAR_FILTER_HOUR_LIMIT_MAX_DEFAULT = "20"

    const val KEY_STRIPE_API_PUBLISHABLE_KEY = "stripeApiPublishableKey"
    const val KEY_EVENT = "event"
    const val KEY_EVENT_ID = "eventId"
    const val KEY_CARD_HOLDER = "cardholder"
    const val KEY_TICKET_PRICE = "ticketPrice"
    const val KEY_TICKET_IDS = "ticketIds"
    const val KEY_TICKET_AMOUNT = "numberOfTickets"
    const val KEY_TERMS_LINK = "termsOfServiceLink"

    const val SHOW_DRAWER = "showDrawer"

    const val KEY_NOTIFICATION_TYPE_ID = "type_id"

    const val SHOW_UPDATE_NOTE = "showUpdateNote"
    const val UPDATE_MESSAGE = "updateNoteMessage"
    const val SAVED_APP_VERSION = "savedAppVersion"

    const val CAFETERIA_CARDS_SETTING = "cafeteria_cards_selection"
    const val NO_CAFETERIA_FOUND = "-1"

    const val SETTINGS_RESTART = "settingsRestart"

    const val DESIGN_THEME = "design_theme_preference"

    // Used in determining when to prompt for google playstore review
    const val HAS_VISITED_CALENDAR = "has_visited_calendar"
    const val HAS_VISITED_GRADES = "has_visited_grades"
    const val LAST_REVIEW_PROMPT = "last_review_prompt"
}
