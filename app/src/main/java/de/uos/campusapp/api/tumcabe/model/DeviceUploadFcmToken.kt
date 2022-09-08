package de.uos.campusapp.api.tumcabe.model

import android.content.Context
import de.uos.campusapp.api.auth.legacy.AuthenticationManager
import de.uos.campusapp.api.auth.legacy.exception.NoPrivateKey

data class DeviceUploadFcmToken(
    val verification: TUMCabeVerification,
    val token: String,
    val signature: String
) {

    companion object {

        @Throws(NoPrivateKey::class)
        fun getDeviceUploadFcmToken(c: Context, token: String): DeviceUploadFcmToken {
            val verification = TUMCabeVerification.create(c) ?: throw NoPrivateKey()
            return DeviceUploadFcmToken(
                    verification = verification,
                    token = token,
                    signature = AuthenticationManager(c).sign(token)
            )
        }
    }
}
