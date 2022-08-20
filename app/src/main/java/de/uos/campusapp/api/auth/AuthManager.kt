package de.uos.campusapp.api.auth

import android.content.Context
import de.uos.campusapp.component.ui.onboarding.model.IdentityInterface
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import okhttp3.Interceptor

abstract class AuthManager {
    abstract fun hasAccess(): Boolean
    abstract fun clear()

    abstract fun getInterceptor(): Interceptor
    
    companion object {

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