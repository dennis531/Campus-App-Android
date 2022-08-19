package de.tum.`in`.tumcampusapp.utils

import android.content.Context
import de.tum.`in`.tumcampusapp.api.auth.AuthManager
import de.tum.`in`.tumcampusapp.api.auth.OAuthManager
import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
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
    fun getApiClient(context: Context, component: Component): BaseAPI {
        return when (component) {
            Component.TRANSPORTATION -> getTransportationClient(context)
            Component.CAFETERIA -> getCafeteriaClient(context)
            else -> getCampusClient(context, component)
        }
    }

    private fun getCampusClient(context: Context, component: Component): BaseAPI {
        return when (getApi(component)) {
            // Add more api clients here
            Api.STUDIP -> StudipClient.getInstance(context)
            else -> throw IllegalStateException("Campus API not known.")
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
            else -> throw IllegalStateException("Cafeteria API not known.")
        }
    }

    /**
     * Collects all configured APIs which requires authentication
     */
    private fun getAuthApis(): List<Api> {
        val apis = ArrayList<Api>()

        Component.values().forEach {
            if (it.needsAuthentication) {
                apis += getApi(it)
            }
        }

        return apis.distinct()
    }

    /**
     * Get the main api or a individual component api if set
     */
    private fun getApi(component: Component): Api =
        getConfig("${component.name}_API", null) ?: getConfig(ConfigConst.API, Api.STUDIP)

    /**
     * Gets all needed authentication methods
     */
    fun getAuthMethods(): List<AuthMethod> {
        val authMethods = ArrayList<AuthMethod>()

        val apis = getAuthApis()
        apis.forEach {
            authMethods += getAuthMethod(it)
        }

        return authMethods.distinct()
    }

    /**
     * Gets all authentication manager of configured authentication methods
     */
    fun getAuthManagers(context: Context): List<AuthManager> {
        return getAuthMethods().map { getAuthManager(context, it) }
    }

    /**
     * Get the main auth method or an individual api auth method if set
     */
    private fun getAuthMethod(api: Api): AuthMethod =
        getConfig("${api.name}_AUTH_METHOD", null) ?: getConfig(ConfigConst.AUTH_METHOD, AuthMethod.OAUTH10A)

    @JvmStatic
    fun getAuthManager(context: Context, component: Component): AuthManager {
        return getAuthManager(context, getApi(component))
    }

    @JvmStatic
    fun getAuthManager(context: Context, api: Api): AuthManager {
        return getAuthManager(context, getAuthMethod(api))
    }

    fun getAuthManager(context: Context, authMethod: AuthMethod): AuthManager {
        return when (authMethod) {
            // Add more authentication methods here
            AuthMethod.OAUTH10A -> OAuthManager(context)
            else -> throw IllegalStateException("Authentication method not known.")
        }
    }

    /**
     * Checks if the component is enabled in the config file, the required api interface is provided by the client
     * and the user has been authenticated. The authentication will be checked if the component needs an authenticated user
     * which is set with [Component.needsAuthentication].
     *
     * @param checkAuthentication Should the user authentication status be checked?
     */
    @JvmStatic
    fun isComponentEnabled(context: Context, component: Component, checkAuthentication: Boolean = true): Boolean {
        // Check if component is disabled in configuration
        if (!getComponentConfig(component)) {
            Utils.log("Component not enabled in config: ${component.name}")
            return false
        }

        // Components without api dependency
        if (!component.requiresAPI) {
            return true
        }

        // Check if component needs an authenticated user and access is granted
        if (checkAuthentication && needsAuthentication(component) && !getAuthManager(context, component).hasAccess()) {
            return false
        }

        // Components needing api client
        return getApiClient(context, component).hasAPI(component)
    }

    @JvmStatic
    private fun needsAuthentication(component: Component?): Boolean {
        if (component == null) {
            return false
        }

        return component.needsAuthentication
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