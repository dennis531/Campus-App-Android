package de.uos.campusapp.api.general

enum class CacheControl(val header: String) {
    BYPASS_CACHE("no-cache"),
    USE_CACHE("public")
}