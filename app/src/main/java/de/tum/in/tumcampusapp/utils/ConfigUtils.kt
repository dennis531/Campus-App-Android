package de.tum.`in`.tumcampusapp.utils

import android.content.Context
import de.tum.`in`.tumcampusapp.api.auth.AuthManager
import de.tum.`in`.tumcampusapp.api.auth.OAuthManager
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.api.studip.StudipClient
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.api.generic.CafeteriaAPI
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.api.munich.MunichCafeteriaAPIClient
import de.tum.`in`.tumcampusapp.component.ui.transportation.api.mvv.MvvClient
import de.tum.`in`.tumcampusapp.config.Api
import de.tum.`in`.tumcampusapp.config.AuthMethod
import de.tum.`in`.tumcampusapp.config.Config
import de.tum.`in`.tumcampusapp.config.TransportationApi as TransportApiEnum
import de.tum.`in`.tumcampusapp.config.CafeteriaApi as CafeteriaApiEnum
import de.tum.`in`.tumcampusapp.component.ui.transportation.api.generic.TransportationAPI
import de.tum.`in`.tumcampusapp.component.ui.transportation.api.vbn.VbnClient

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

    fun getTransportationClient(context: Context): TransportationAPI {
        return when (getConfig(ConfigConst.TRANSPORTATION_API, TransportApiEnum.MVV)) {
            // Add more transportation api clients here
            TransportApiEnum.MVV -> MvvClient.getInstance(context)
            TransportApiEnum.VBN -> VbnClient.getInstance(context)
            else -> throw IllegalStateException("Transportation API not known.")
        }
    }

    fun getCafeteriaClient(context: Context): CafeteriaAPI {
        return when (getConfig(ConfigConst.CAFETERIA_API, CafeteriaApiEnum.MUNICH)) {
            // Add more transportation api clients here
            CafeteriaApiEnum.MUNICH -> MunichCafeteriaAPIClient.getInstance(context)
            else -> throw IllegalStateException("Transportation API not known.")
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

        // Components without LMS API dependency
        if (!component.requiresLMS) {
            return true
        }

        // Check if component needs LMS access and access is granted
        if (needsComponentLMSAccess(component) && !getAuthManager(context).hasAccess()) {
            return false
        }

        // Components needing LMS API
        return getLMSClient(context).hasAPI(component)
    }

    @JvmStatic
    private fun needsComponentLMSAccess(component: Component?): Boolean {
        if (component == null) {
            return false
        }

        return component.needsLMSAccess
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
            Component.GEOFENCING -> getConfig(ConfigConst.GEOFENCING_ENABLED, defaultVal)
            Component.TUITIONFEES -> getConfig(ConfigConst.TUITIONFEES_ENABLED, defaultVal)
            Component.CAFETERIA -> getConfig(ConfigConst.CAFETERIA_ENABLED, defaultVal)
            Component.CHAT -> getConfig(ConfigConst.CHAT_ENABLED, defaultVal)
            Component.EDUROAM -> getConfig(ConfigConst.EDUROAM_ENABLED, defaultVal)
            Component.NEWS -> getConfig(ConfigConst.NEWS_ENABLED, defaultVal)
            Component.OPENINGHOUR -> getConfig(ConfigConst.OPENINGHOUR_ENABLED, defaultVal)
            Component.TRANSPORTATION -> getConfig(ConfigConst.TRANSPORTATION_ENABLED, defaultVal)
            Component.STUDYROOM -> getConfig(ConfigConst.STUDYROOM_ENABLED, defaultVal)
            Component.MESSAGES -> getConfig(ConfigConst.MESSAGES_ENABLED, defaultVal)
            else -> true // Remaining components can not be disabled manually
        }
    }

    /**
     * Checks if the tuition fees should be loaded from the api
     */
    @JvmStatic
    fun shouldTuitionLoadedFromApi(): Boolean {
        if (!getConfig(ConfigConst.TUITIONFEES_FROM_API, false)) {
            return false
        }

        return true
    }
}