package de.uos.campusapp.component.ui.eduroam;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import de.uos.campusapp.R;
import de.uos.campusapp.utils.ConfigConst;
import de.uos.campusapp.utils.ConfigUtils;
import de.uos.campusapp.utils.Const;
import de.uos.campusapp.utils.Utils;

import static android.provider.Settings.ACTION_WIFI_ADD_NETWORKS;
import static android.provider.Settings.EXTRA_WIFI_NETWORK_LIST;

/**
 * Eduroam manager, manages connecting to eduroam wifi network
 */
public class EduroamController {

    private final Context mContext;

    EduroamController(Context context) {
        mContext = context;
    }

    /**
     * Tests if eduroam has already been setup
     *
     * @return true if eduroam is already setup, false otherwise
     */
    @Nullable
    public static WifiConfiguration getEduroamConfig(Context c) {
        WifiManager wifiManager = (WifiManager) c.getApplicationContext()
                                                 .getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        //We didn't get a list, so maybe there's no wifi?
        if (list == null) {
            return null;
        }

        for (WifiConfiguration config : list) {
            if (config.SSID != null && config.SSID.equals("\"" + Const.EDUROAM_SSID + "\"")) {
                return config;
            }
        }
        return null;
    }

    /**
     * Configures eduroam wifi connection.
     *
     * @param identity       User's identity
     * @param networkPass User's password
     * @return Returns true if configuration was successful, false otherwise
     */
    boolean configureEduroam(String identity, String networkPass) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Used functions are deprecated since api level 29.
            return addWifiConfiguration(identity, networkPass);
        } else {
           return addNetworkSuggestion(identity, networkPass);
        }
    }

    /**
     * Adds or updates a eduroam wifi config to the wifi manager.
     * Used wifi manager functions are deprecated since api level 29.
     *
     * @param identity       User's identity
     * @param networkPass User's password
     * @return true if successful
     */
    private boolean addWifiConfiguration(String identity, String networkPass) {
        // Configure Wifi
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext()
                                                        .getSystemService(Context.WIFI_SERVICE);

        int networkId;

        boolean update = true;
        WifiConfiguration conf = getEduroamConfig(mContext);

        if (conf == null) {
            update = false;
            conf = new WifiConfiguration();
        }

        conf.SSID = "\"" + Const.EDUROAM_SSID + "\"";
        conf.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
        conf.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
        conf.allowedGroupCiphers.set(GroupCipher.TKIP);
        conf.allowedGroupCiphers.set(GroupCipher.CCMP);
        conf.allowedPairwiseCiphers.set(PairwiseCipher.CCMP);
        conf.allowedProtocols.set(Protocol.RSN);
        conf.status = WifiConfiguration.Status.ENABLED;

        setupEnterpriseConfig(conf.enterpriseConfig, identity, networkPass);

        // Add eduroam to wifi networks
        if (update) {
            networkId = wifiManager.updateNetwork(conf);
            Utils.log("deleted " + conf.networkId);
        } else {
            networkId = wifiManager.addNetwork(conf);
        }

        Utils.log("added " + networkId);

        //Check if update successful
        if (networkId == -1) {
            return false;
        }

        //Enable and exit
        wifiManager.enableNetwork(networkId, true);
        return true;
    }

//    private void setupEnterpriseConfigAPI18(WifiConfiguration conf, String identity, String networkPass) {
//        conf.enterpriseConfig.setIdentity(identity);
//        conf.enterpriseConfig.setPassword(networkPass);
//        conf.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PWD);
//    }

    /**
     * Adds a eduroam network suggestion to the wifi manager. The suggestion is not visible in the
     * wifi settings and only available when the app is installed and enabled.
     *
     * @param identity User's identity
     * @param networkPass User's password
     * @return true if successful
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean addNetworkSuggestion(String identity, String networkPass) {
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext()
                                                        .getSystemService(Context.WIFI_SERVICE);

        // remove all old network suggestions
        wifiManager.removeNetworkSuggestions(Collections.emptyList());

        // add new network suggestion
        WifiNetworkSuggestion suggestion = getEduroamSuggestion(identity, networkPass);
        int status = wifiManager.addNetworkSuggestions(Collections.singletonList(suggestion));
        Utils.log("Status: " + status);

        return status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS;
    }

    /**
     * Creates a wifi network suggestion containing the eduroam wifi settings
     *
     * @param identity User's identity
     * @param networkPass User's password
     * @return wifi network suggestion
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private WifiNetworkSuggestion getEduroamSuggestion(String identity, String networkPass) {
        WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
        setupEnterpriseConfig(enterpriseConfig, identity, networkPass);

        WifiNetworkSuggestion.Builder builder = new WifiNetworkSuggestion.Builder();
        return builder
                .setSsid(Const.EDUROAM_SSID)
                .setWpa2EnterpriseConfig(enterpriseConfig)
                .build();
    }

    private void setupEnterpriseConfig(WifiEnterpriseConfig enterpriseConfig, String identity, String networkPass) {
        String radiusDomain = ConfigUtils.getConfig(ConfigConst.EDUROAM_RADIUS_DOMAIN, "");

        List<String> anonymousIds = ConfigUtils.getConfig(ConfigConst.EDUROAM_ANONYMOUS_IDENTITIES, Collections.emptyList());
        String anonymousId = !anonymousIds.isEmpty() ? anonymousIds.get(0) : "";

        enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TTLS);
        enterpriseConfig.setIdentity(identity);
        enterpriseConfig.setPassword(networkPass);
        enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2);
        enterpriseConfig.setAnonymousIdentity(anonymousId);
        enterpriseConfig.setCaCertificate(getEduroamCertificate());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            enterpriseConfig.setDomainSuffixMatch(radiusDomain);
        } else {
            enterpriseConfig.setSubjectMatch(radiusDomain);
        }
    }

    @Nullable
    private X509Certificate getEduroamCertificate() {
        try (InputStream inStream = mContext.getResources().openRawResource(R.raw.rootcert)) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certificateFactory.generateCertificate(inStream);
        } catch (IOException | CertificateException e) {
            return null;
        }
    }

    /**
     * Creates a wifi add networks intent including the eduroam wifi configurations
     *
     * @param identity User's identity
     * @param networkPass User's password
     * @return The wifi add networks intent
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public Intent getEduroamIntent(String identity, String networkPass) {
        ArrayList<WifiNetworkSuggestion> suggestions = new ArrayList<WifiNetworkSuggestion>();

        suggestions.add(getEduroamSuggestion(identity, networkPass));

        // Create intent
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_WIFI_NETWORK_LIST, suggestions);
        Intent intent = new Intent(ACTION_WIFI_ADD_NETWORKS);
        intent.putExtras(bundle);
        return intent;
    }
}
