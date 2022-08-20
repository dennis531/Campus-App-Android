package de.uos.campusapp.component.ui.eduroam

import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiEnterpriseConfig.Eap.PEAP
import android.net.wifi.WifiEnterpriseConfig.Eap.TTLS
import android.net.wifi.WifiEnterpriseConfig.Phase2.MSCHAPV2
import android.net.wifi.WifiEnterpriseConfig.Phase2.PAP
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.overview.CardInteractionListener
import de.uos.campusapp.component.ui.overview.CardManager
import de.uos.campusapp.component.ui.overview.card.Card
import de.uos.campusapp.component.ui.overview.card.CardViewHolder
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigConst
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Utils
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.wifiManager
import java.util.*

class EduroamFixCard(
    context: Context
) : Card(CardManager.CARD_EDUROAM_FIX, context, Component.EDUROAM, "card_eduroam_fix_start") {

    private val errors: MutableList<String> = ArrayList()
    private lateinit var eduroam: WifiConfiguration

    private val radiusDNS: String = ConfigUtils.getConfig(ConfigConst.EDUROAM_RADIUS_DOMAIN, "")
    private val idDomains: List<String> = ConfigUtils.getConfig(ConfigConst.EDUROAM_ID_DOMAINS, listOf())
    private val anonymousIdentities: List<String> = ConfigUtils.getConfig(ConfigConst.EDUROAM_ANONYMOUS_IDENTITIES, listOf())

    private fun isConfigValid(): Boolean {
        errors.clear()
        // If it is not configured then the config valid
        eduroam = EduroamController.getEduroamConfig(context) ?: return true

        // No eduroam domains are available to check
        if (idDomains.isEmpty()) {
            Utils.log("No eduroam domains are set")
            return true
        }

        // Eduroam was configured by other university
        if (!isCampusEduroam(eduroam.enterpriseConfig.identity)) {
            Utils.log("Eduroam wasn't configured at this campus")
            return true
        }

        // Check attributes - check newer match for the radius server
        // for all configurations
        // Check that the full quantifier is used (we already know it's a campus config)
        if (!eduroam.enterpriseConfig.identity.contains(AT_SIGN)) {
            errors.add(context.getString(R.string.wifi_identity_zone))
        }

        val eapMethod = eduroam.enterpriseConfig.eapMethod
        val phase2 = eduroam.enterpriseConfig.phase2Method

        if (eapMethod == TTLS && (phase2 == MSCHAPV2 || phase2 == PAP) || eapMethod == PEAP && phase2 == MSCHAPV2) {
            checkDNSName()
            checkAnonymousIdentity()
            // note: checking the certificate does not seem possible
        }
        // else: PWD or unknown authentication method (we don't know if that method is safe or not -> ignore)

        return errors.isEmpty()
    }

    override fun updateViewHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is EduroamFixCardViewHolder) {
            viewHolder.bind(eduroam, errors)
        }
    }

    override fun shouldShow(prefs: SharedPreferences): Boolean {
        // Check if wifi is turned on at all, as we cannot say if it was configured if its off
        return if (!context.wifiManager.isWifiEnabled) {
            false
        } else !isConfigValid()
    }

    override fun discard(editor: SharedPreferences.Editor) {
        context.defaultSharedPreferences.edit().putBoolean("card_eduroam_fix_start", false).apply()
    }

    override fun getId(): Int {
        return 0
    }

    private fun checkAnonymousIdentity() {
        if (anonymousIdentities.isEmpty()) {
            return
        }

        val anonymousIdentity = eduroam.enterpriseConfig.anonymousIdentity
        if (anonymousIdentity != null && !anonymousIdentities.contains(anonymousIdentity)) {
            errors.add(context.getString(R.string.wifi_anonymous_identity_not_set))
        }
    }

    private fun checkDNSName() {
        // No radius server address is set
        if (radiusDNS.isBlank()) {
            return
        }

        if (SDK_INT < M && !isValidSubjectMatchAPI18(eduroam)) {
            errors.add(context.getString(R.string.wifi_dns_name_not_set))
        } else if (SDK_INT >= M &&
                (eduroam.enterpriseConfig.altSubjectMatch != "DNS:$radiusDNS" || eduroam.enterpriseConfig.domainSuffixMatch != radiusDNS) &&
                !isValidSubjectMatchAPI18(eduroam)) {
            errors.add(context.getString(R.string.wifi_dns_name_not_set))
        }
    }

    private fun isCampusEduroam(identity: String): Boolean {
        return idDomains.any {identity.endsWith(it)}
    }

    private fun isValidSubjectMatchAPI18(eduroam: WifiConfiguration): Boolean {
        // AltSubjectMatch is not available for API18
        Utils.log("SubjectMatch: " + eduroam.enterpriseConfig.subjectMatch)
        return eduroam.enterpriseConfig.subjectMatch == radiusDNS
    }

    companion object {
        private const val AT_SIGN = "@"
        @JvmStatic
        fun inflateViewHolder(parent: ViewGroup, interactionListener: CardInteractionListener): CardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_eduroam_fix, parent, false)
            return EduroamFixCardViewHolder(view, interactionListener)
        }
    }
}
