package de.tum.`in`.tumcampusapp.utils

import android.content.Context
import de.tum.`in`.tumcampusapp.api.auth.AuthManager
import de.tum.`in`.tumcampusapp.api.auth.OAuthManager
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.api.studip.StudipClient
import de.tum.`in`.tumcampusapp.config.Api
import de.tum.`in`.tumcampusapp.config.AuthMethod
import de.tum.`in`.tumcampusapp.config.Config

object ConfigUtils {
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getConfig(key: String, defaultVal: T): T {
        return try {
            Config.javaClass.getMethod("get$key").invoke(Config) as? T ?: defaultVal
        } catch (e: NoSuchMethodException) {
            defaultVal
        }
    }

    @JvmStatic
    fun getLMSClient(context: Context): LMSClient {
        return when (getConfig(ConfigConst.API, Api.STUDIP)) {
            // Add more api clients here
            Api.STUDIP -> StudipClient.getInstance(context)
            else -> throw IllegalStateException("API not known.")
        }
    }

    @JvmStatic
    fun getAuthManager(context: Context): AuthManager {
        return when (getConfig(ConfigConst.AUTH_METHOD, AuthMethod.OAUTH10A)) {
            // Add more authentication methods here
            AuthMethod.OAUTH10A -> OAuthManager(context)
            else -> throw IllegalStateException("Authentication method not known.")
        }
    }

    @JvmStatic
    fun isComponentEnabled(context: Context, component: Component): Boolean {
        // Check if component is disabled in configuration
        if (!getComponentConfig(component)) {
            Utils.log("Component not enabled in config: ${component.name}")
            return false
        }

        // Components needing LMS API
        if (component.requiresLMS) {
            return getLMSClient(context).hasAPI(component)
        }

        // Components without LMS dependency
        return true
    }

    @JvmStatic
    fun isCalendarEditable(): Boolean {
        return getConfig(ConfigConst.CALENDAR_EDITABLE, true)
    }

    private fun getComponentConfig(component: Component): Boolean {
        val defaultVal = false // not configured components are disabled by default
        return when (component) {
            Component.CALENDAR -> getConfig(ConfigConst.CALENDAR_ENABLED, defaultVal)
            Component.GRADES -> getConfig(ConfigConst.GRADES_ENABLED, defaultVal)
            Component.LECTURES -> getConfig(ConfigConst.LECTURES_ENABLED, defaultVal)
            Component.PERSON -> getConfig(ConfigConst.PERSON_ENABLED, defaultVal)
            Component.ROOMFINDER -> getConfig(ConfigConst.ROOMFINDER_ENABLED, defaultVal)
            Component.TUTIONFEES -> getConfig(ConfigConst.TUTIONFEES_ENABLED, defaultVal)
            Component.CAFETERIA -> getConfig(ConfigConst.CAFETERIA_ENABLED, defaultVal)
            Component.CHAT -> getConfig(ConfigConst.CHAT_ENABLED, defaultVal)
            Component.EDUROAM -> getConfig(ConfigConst.EDUROAM_ENABLED, defaultVal)
            Component.NEWS -> getConfig(ConfigConst.NEWS_ENABLED, defaultVal)
            Component.OPENINGHOUR -> getConfig(ConfigConst.OPENINGHOUR_ENABLED, defaultVal)
            Component.TRANSPORTATION -> getConfig(ConfigConst.TRANSPORTATION_ENABLED, defaultVal)
            else -> true // Remaining components can not be disabled manually
        }
    }
}