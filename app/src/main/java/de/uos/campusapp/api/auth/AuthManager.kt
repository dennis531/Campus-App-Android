package de.uos.campusapp.api.auth

import android.content.Context
import de.uos.campusapp.component.ui.onboarding.model.IdentityInterface
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import okhttp3.Interceptor
import java.util.UUID

/**
 * Abstract authentication manager providing general authentication function like
 * requesting the authenication status of the user.
 *
 * This class does not provide any authentication mechanisms.
 * The implementation of the these mechanism should be handled in the subclasses.
 */
abstract class AuthManager {
    /**
     * Checks if the user is authenticated
     */
    abstract fun hasAccess(): Boolean

    /**
     * Clears all authentication values like access tokens
     */
    abstract fun clear()

    /**
     * Returns the authentication interceptor for the okhttp3 client
     */
    abstract fun getInterceptor(): Interceptor

    companion object {

        private var uniqueID: String? = null

        /**
         * Saves all user information in preferences
         */
        fun saveIdentity(context: Context, identity: IdentityInterface) {
            // Collect personal data
            Utils.setSetting(context, Const.USERNAME, identity.username)
            Utils.setSetting(context, Const.EMPLOYEE_MODE, false)

            Utils.setSetting(context, Const.PROFILE_ID, identity.id)
            Utils.setSetting(context, Const.PROFILE_PICTURE_URL, identity.imageUrl)
            Utils.setSetting(context, Const.PROFILE_EMAIL, identity.email)
            Utils.setSetting(context, Const.PROFILE_DISPLAY_NAME, identity.fullName)
        }

        /**
         * Gets an unique id that identifies this device.
         * Should only reset after a reinstall or wiping of the settingsPrefix.
         *
         * @return Unique device id
         */
        @Synchronized
        fun getDeviceID(context: Context): String {
            if (uniqueID == null) {
                uniqueID = Utils.getSetting(context, Const.PREF_UNIQUE_ID, "")
                if ("" == uniqueID) {
                    uniqueID = UUID.randomUUID().toString()
                    Utils.setSetting(context, Const.PREF_UNIQUE_ID, uniqueID!!)
                }
            }
            return uniqueID!!
        }
    }
}