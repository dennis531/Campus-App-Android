package de.tum.`in`.tumcampusapp.api.auth

import android.content.Context
import de.tum.`in`.tumcampusapp.component.ui.onboarding.model.IdentityInterface
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
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