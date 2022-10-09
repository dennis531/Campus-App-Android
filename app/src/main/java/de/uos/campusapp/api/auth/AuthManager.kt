package de.uos.campusapp.api.auth

import android.content.Context
import de.uos.campusapp.component.ui.onboarding.model.IdentityInterface
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import okhttp3.Interceptor

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
    }
}