package de.uos.campusapp.api.legacy.tumcabe.model

import android.content.Context
import de.uos.campusapp.api.auth.legacy.AuthenticationManager
import de.uos.campusapp.api.auth.legacy.exception.NoPrivateKey
import de.uos.campusapp.utils.Utils
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*

data class TUMCabeVerification(
    val signature: String,
    val date: String,
    val rand: String,
    val device: String,
    var data: Any? = null
) {

    companion object {

        @JvmStatic
        fun create(context: Context, data: Any? = null): TUMCabeVerification? {
            val date = Date().toString()
            val rand = BigInteger(130, SecureRandom()).toString(32)
            val deviceID = AuthenticationManager.getDeviceID(context)

            val signature = try {
                AuthenticationManager(context).sign(date + rand + deviceID)
            } catch (e: NoPrivateKey) {
                Utils.log(e)
                return null
            }

            return TUMCabeVerification(signature, date, rand, deviceID, data)
        }
    }
}