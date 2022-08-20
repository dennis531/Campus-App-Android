package de.uos.campusapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.uos.campusapp.utils.ConfigConst
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Const.CAMPUS_GEOFENCE
import de.uos.campusapp.utils.Utils

class GeofencingStartupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!isValidIntent(intent)) {
            return
        }

        Utils.log("Restarting geofencing due to " + intent?.action)
        context?.let {
            val geofencingIntent = GeofencingRegistrationService.buildGeofence(
                it,
                CAMPUS_GEOFENCE,
                ConfigUtils.getConfig(ConfigConst.GEOFENCING_LATITUDE, 0.0),
                ConfigUtils.getConfig(ConfigConst.GEOFENCING_LONGITUDE, 0.0),
                ConfigUtils.getConfig(ConfigConst.GEOFENCING_RADIUS_IN_METER, 0f)
            )
            GeofencingRegistrationService.startGeofencing(it, geofencingIntent)
        }
    }

    private fun isValidIntent(intent: Intent?): Boolean {
        return intent != null && (intent.action == "android.intent.action.BOOT_COMPLETED" ||
                intent.action == "android.location.MODE_CHANGED" ||
                intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
                intent.action == "android.location.PROVIDERS_CHANGED")
    }
}