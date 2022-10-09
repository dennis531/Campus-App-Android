package de.uos.campusapp.component.other.settings

import android.Manifest.permission.READ_CALENDAR
import android.Manifest.permission.WRITE_CALENDAR
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.preference.*
import de.uos.campusapp.R
import de.uos.campusapp.api.auth.AuthManager
import de.uos.campusapp.component.other.locations.model.Campus
import de.uos.campusapp.component.ui.calendar.CalendarController
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaRole
import de.uos.campusapp.component.ui.cafeteria.repository.CafeteriaLocalRepository
import de.uos.campusapp.component.ui.eduroam.SetupEduroamActivity
import de.uos.campusapp.component.ui.onboarding.StartupActivity
import de.uos.campusapp.database.CaDb
import de.uos.campusapp.di.injector
import de.uos.campusapp.service.SilenceService
import de.uos.campusapp.service.StartSyncReceiver
import de.uos.campusapp.utils.*
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.notificationManager
import java.util.concurrent.ExecutionException
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val themeProvider by lazy { ThemeProvider(requireContext()) }

    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var cafeteriaLocalRepository: CafeteriaLocalRepository

    private val calendarAuthManager: AuthManager by lazy {
        ConfigUtils.getAuthManager(requireContext(), Component.CALENDAR)
    }

    private val authManagers: List<AuthManager> by lazy {
        ConfigUtils.getAuthManagers(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.inject(this)
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.settings, rootKey)
//        setUpEmployeeSettings()

        // Disables silence service and logout if the app is used without LMS access
        val silentSwitch = findPreference(Const.SILENCE_SERVICE) as? SwitchPreferenceCompat
        if (!calendarAuthManager.hasAccess()) {
            silentSwitch?.isEnabled = false
        }

        val logoutButton = findPreference(BUTTON_LOGOUT)
        if (authManagers.all { !it.hasAccess() }) {
            logoutButton?.isVisible = false
        }

        // Only do these things if we are in the root of the preferences
        if (rootKey == null) {
            // Click listener for preference list entries. Used to simulate a button
            // (since it is not possible to add a button to the preferences screen)
            findPreference(BUTTON_LOGOUT).onPreferenceClickListener = this
            setSummary("language_preference")
            setSummary(Const.DESIGN_THEME)
            setSummary(Const.SILENCE_SERVICE_MODE)
            setSummary("background_mode_set_to")
            initDefaultCampusSelections()
            initCardPreferences()
            initSilentServicePreferences()
        } else if (rootKey == "card_cafeteria") {
            initCafeteriaCardSelections()
            initDefaultCafeteriaSelections()
            initCafeteriaCardRoles()
        } else if (rootKey == "card_transportation") {
            initTransportationCard()
        } else if (rootKey == "card_eduroam") {
            findPreference(SETUP_EDUROAM).onPreferenceClickListener = this
        }

        requireContext().defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun initCardPreferences() {
        Component.values().forEach { component ->
            component.preferenceKey?.let {
                findPreference(it).isVisible = ConfigUtils.isComponentEnabled(requireContext(), component, false)
            }
        }
    }

    private fun initSilentServicePreferences() {
        val isVisible = ConfigUtils.isComponentEnabled(requireContext(), Component.CALENDAR, false)
        findPreference(Const.SILENCE_SERVICE).isVisible = isVisible
        findPreference(Const.SILENCE_SERVICE_MODE).isVisible = isVisible
    }

    /**
     * Disable setting for non-employees.
     *
     * TODO: Disable mode for students; Const.TUMO_EMPLOYEE_ID doesn't work anymore
     */
    private fun setUpEmployeeSettings() {
        val isEmployee = Utils.getSetting(requireContext(), Const.TUMO_EMPLOYEE_ID, "").isNotEmpty()
        val checkbox = findPreference(Const.EMPLOYEE_MODE) ?: return
        if (!isEmployee) {
            checkbox.isEnabled = false
        }
    }

    override fun onSharedPreferenceChanged(
        sharedPrefs: SharedPreferences,
        key: String?
    ) {
        if (key == null) {
            return
        }
        setSummary(key)

        // If the silent mode was activated, start the service. This will invoke
        // the service to call onHandleIntent which checks available lectures
        if (key == Const.SILENCE_SERVICE) {
            val service = Intent(requireContext(), SilenceService::class.java)
            if (sharedPrefs.getBoolean(key, false)) {
                if (!SilenceService.hasPermissions(requireContext())) {
                    // disable until silence service permission is resolved
                    val silenceSwitch = findPreference(Const.SILENCE_SERVICE) as SwitchPreferenceCompat
                    silenceSwitch.isChecked = false
                    Utils.setSetting(requireContext(), Const.SILENCE_SERVICE, false)
                    SilenceService.requestPermissions(requireContext())
                } else {
                    requireContext().startService(service)
                }
            } else {
                requireContext().stopService(service)
            }
        }

        // Change design theme
        if (key == Const.DESIGN_THEME) {
            val theme = themeProvider.getTheme(sharedPrefs.getString(key, "system")!!)
            AppCompatDelegate.setDefaultNightMode(theme)
        }

        // If the background mode was activated, start the service. This will invoke
        // the service to call onHandleIntent which updates all background data
        if (key == Const.BACKGROUND_MODE) {
            if (sharedPrefs.getBoolean(key, false)) {
                StartSyncReceiver.startBackground(requireContext())
            } else {
                StartSyncReceiver.cancelBackground()
            }
        }

        // restart app after language change
        if (key == "language_preference" && activity != null) {
            (activity as SettingsActivity).restartApp()
        }
    }

    private fun initDefaultCampusSelections() {
        val campuses = ConfigUtils.getConfig(ConfigConst.CAMPUS, emptyList<Campus>())

        val noDefaultCampusName = getString(R.string.no_default_campus)
        val campusNames = listOf(noDefaultCampusName) + campuses.map { it.getName(requireContext()) }

        val noDefaultCampusId = Const.NO_DEFAULT_CAMPUS_ID
        val campusIds = listOf(noDefaultCampusId) + campuses.map { it.id }

        val defaultValue = campusIds.getOrNull(1) ?: noDefaultCampusId

        val preference = findPreference(Const.DEFAULT_CAMPUS) as ListPreference
        preference.entries = campusNames.toTypedArray()
        preference.entryValues = campusIds.toTypedArray()
        preference.setDefaultValue(defaultValue)
        preference.run { summary = entries[findIndexOfValue(Utils.getSetting(context, key, defaultValue))] }

        // Force to check the default value if no value is set
        if (preference.value == null) {
            preference.value = defaultValue
        }
    }

    private fun initCafeteriaCardSelections() {
        val cafeterias = cafeteriaLocalRepository
            .getAllCafeterias()
            .blockingFirst()
            .sortedBy { it.name }

        val cafeteriaByLocationName = getString(R.string.settings_cafeteria_depending_on_location)
        val cafeteriaNames = listOf(cafeteriaByLocationName) + cafeterias.map { it.name }

        val cafeteriaByLocationId = Const.CAFETERIA_BY_LOCATION_SETTINGS_ID
        val cafeteriaIds = listOf(cafeteriaByLocationId) + cafeterias.map { it.id }

        val preference = findPreference(Const.CAFETERIA_CARDS_SETTING) as MultiSelectListPreference
        preference.entries = cafeteriaNames.toTypedArray()
        preference.entryValues = cafeteriaIds.toTypedArray()

        preference.setOnPreferenceChangeListener { pref, newValue ->
            (pref as MultiSelectListPreference).values = newValue as Set<String>
            setCafeteriaCardsSummary(preference)
            false
        }

        setCafeteriaCardsSummary(preference)
    }

    private fun initDefaultCafeteriaSelections() {
        val preferenceCategory = findPreference("cafeteria_defaults") as PreferenceCategory

        val campuses = ConfigUtils.getConfig(ConfigConst.CAMPUS, emptyList<Campus>())

        for (c in campuses) {
            // Show default cafeteria selection if more than one cafeteria is available
            if (!c.cafeterias.isNullOrEmpty() && c.cafeterias.size > 1) {
                val defaultValue = c.cafeterias.first().id
                val preference = ListPreference(preferenceCategory.context).apply {
                    setDefaultValue(defaultValue)
                    entries = c.cafeterias.map { it.name }.toTypedArray()
                    entryValues = c.cafeterias.map { it.id }.toTypedArray()
                    key = "card_cafeteria_default_${c.id}"
                    title = c.getName(requireContext())
                    summary = entries[findIndexOfValue(Utils.getSetting(context, key, defaultValue))]
                }

                preferenceCategory.addPreference(preference)
            }
        }

        preferenceCategory.isVisible = preferenceCategory.preferenceCount > 0
    }

    private fun initCafeteriaCardRoles() {
        // Show only used roles
        val roles = cafeteriaLocalRepository.getRoles()
            .map { CafeteriaRole.fromId(it) }

        if (roles.isEmpty()) {
            val preferenceCategory = findPreference("cafeteria_extra") as PreferenceCategory
            preferenceCategory.isVisible = false
            return
        }

        // Assume that student role is always given
        val defaultRole = CafeteriaRole.STUDENT.id.toString()

        val preference = findPreference(Const.ROLE) as ListPreference
        preference.entries = roles.map { getString(it.nameResId) }.toTypedArray()
        preference.entryValues = roles.map { it.id.toString() }.toTypedArray()
        preference.setDefaultValue(defaultRole)
        preference.run { summary = entries[findIndexOfValue(Utils.getSetting(context, key, defaultRole))] }

        // Force to check the default value if no value is set
        if (preference.value == null) {
            preference.value = defaultRole
        }
    }

    private fun initTransportationCard() {
        val preferenceCategory = findPreference("station_defaults") as PreferenceCategory

        val campuses = ConfigUtils.getConfig(ConfigConst.CAMPUS, emptyList<Campus>())

        for (c in campuses) {
            // Show default station selection if more than one station is available
            if (!c.stations.isNullOrEmpty() && c.stations.size >= 2) {
                val defaultStation = c.stations.first().name
                val preference = ListPreference(preferenceCategory.context).apply {
                    setDefaultValue(defaultStation)
                    entries = c.stations.map { it.name }.toTypedArray()
                    entryValues = c.stations.map { it.name }.toTypedArray()
                    key = "card_stations_default_${c.id}"
                    title = c.getName(requireContext())
                    summary = entries[findIndexOfValue(Utils.getSetting(context, key, defaultStation))]
                }

                preferenceCategory.addPreference(preference)
            }
        }

        preferenceCategory.isVisible = preferenceCategory.preferenceCount > 0
    }

    private fun setSummary(key: CharSequence) {
        val pref = findPreference(key)
        if (pref is ListPreference) {
            pref.summary = pref.entry
        }
    }

    private fun setCafeteriaCardsSummary(preference: MultiSelectListPreference) {
        val values = preference.values
        if (values.isEmpty()) {
            preference.setSummary(R.string.settings_no_location_selected)
        } else {
            preference.summary = values
                .map { preference.findIndexOfValue(it) }
                .map { preference.entries[it] }
                .map { it.toString() }
                .sorted()
                .joinToString(", ")
        }
    }

    override fun onPreferenceClick(
        preference: Preference?
    ): Boolean {
        when (preference?.key) {
            SETUP_EDUROAM -> startActivity(Intent(context, SetupEduroamActivity::class.java))
            BUTTON_LOGOUT -> showLogoutDialog(R.string.logout_title, R.string.logout_message)
            else -> return false
        }
        return true
    }

    private fun showLogoutDialog(title: Int, message: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.logout) { _, _ -> logout() }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .apply {
                window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
            }
            .show()
    }

    private fun logout() {
        try {
            clearData()
        } catch (e: Exception) {
            Utils.log(e)
            showLogoutDialog(R.string.logout_error_title, R.string.logout_try_again)
            return
        }

        startActivity(Intent(requireContext(), StartupActivity::class.java))
        requireActivity().finish()
    }

    @SuppressLint("ApplySharedPref")
    @Throws(ExecutionException::class, InterruptedException::class)
    private fun clearData() {
        CaDb.resetDb(requireContext())
        requireContext().defaultSharedPreferences.edit().clear().commit()

        // Remove all notifications that are currently shown
        requireContext().notificationManager.cancelAll()

        val readCalendar = checkSelfPermission(requireActivity(), READ_CALENDAR)
        val writeCalendar = checkSelfPermission(requireActivity(), WRITE_CALENDAR)

        // Delete local calendar
        Utils.setSetting(requireContext(), Const.SYNC_CALENDAR, false)
        if (readCalendar == PERMISSION_GRANTED && writeCalendar == PERMISSION_GRANTED) {
            CalendarController.deleteLocalCalendar(requireContext())
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    companion object {

        private const val BUTTON_LOGOUT = "button_logout"
        private const val SETUP_EDUROAM = "card_eduroam_setup"

        fun newInstance(
            key: String?
        ) = SettingsFragment().apply { arguments = bundleOf(ARG_PREFERENCE_ROOT to key) }
    }
}
