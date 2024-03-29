package de.uos.campusapp.component.ui.onboarding

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.LiveDataReactiveStreams
import de.uos.campusapp.BuildConfig.VERSION_CODE
import de.uos.campusapp.R
import de.uos.campusapp.api.general.CacheControl
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.component.other.generic.activity.BaseNavigationActivity
import de.uos.campusapp.databinding.ActivityStartupBinding
import de.uos.campusapp.service.DownloadWorker
import de.uos.campusapp.service.StartSyncReceiver
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import de.uos.campusapp.utils.observe
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class StartupActivity : BaseActivity(R.layout.activity_startup, Component.ONBOARDING) {

    private val initializationFinished = AtomicBoolean(false)
    private var tapCounter = 0 // for easter egg

    @Inject
    lateinit var workerActions: DownloadWorker.WorkerActions

//    @Inject
//    lateinit var authManager: AuthenticationManager

    private lateinit var binding: ActivityStartupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injector.downloadComponent().inject(this)

        // Only use Crashlytics if we are not compiling debug
//        val isDebuggable = applicationInfo.isDebuggable
//        if (!DEBUG && !isDebuggable) {
//            FirebaseCrashlytics.getInstance().setCustomKey("TUMID", Utils.getSetting(this, Const.LRZ_ID, ""))
//            FirebaseCrashlytics.getInstance().setCustomKey("DeviceID", AuthenticationManager.getDeviceID(this))
//        }

        val savedAppVersion = Utils.getSettingInt(this, Const.SAVED_APP_VERSION, VERSION_CODE)
        if (savedAppVersion < VERSION_CODE) {
            Utils.setSetting(this, Const.SHOW_UPDATE_NOTE, true)
            Utils.setSetting(this, Const.UPDATE_MESSAGE, "")
        }
        // Always set current app version, otherwise it will never be initialized and the update
        // note is never displayed
        Utils.setSetting(this, Const.SAVED_APP_VERSION, VERSION_CODE)

        initEasterEgg()
        doAsync {
            initApp()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        binding = ActivityStartupBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun initEasterEgg() {
        if (Utils.getSettingBool(this, Const.RAINBOW_MODE, false)) {
            binding.startupCampusLogo.setImageResource(R.drawable.campus_logo_rainbow)
        }

        binding.container.setOnClickListener {
            if (tapCounter++ % 3 == 0) {
                // Switch to the other logo and invert the setting
                val shouldEnableRainbow = Utils.getSettingBool(this, Const.RAINBOW_MODE, false).not()

                if (shouldEnableRainbow) {
                    binding.startupCampusLogo.setImageResource(R.drawable.campus_logo_rainbow)
                } else {
                    binding.startupCampusLogo.setImageResource(R.drawable.campus_logo_blue)
                }

                Utils.setSetting(this, Const.RAINBOW_MODE, shouldEnableRainbow)
            }
        }
    }

    private fun initApp() {
        // Migrate all settings - we somehow ended up having two different shared prefs: join them
        // back together
//        Utils.migrateSharedPreferences(this)

        // Check that we have a private key setup in order to authenticate this device
//        authManager.generatePrivateKey(null)

        // On first setup show remark that loading could last longer than normally
        runOnUiThread {
            binding.startupLoadingProgressBar.show()
        }

        // Start download workers and listen for finalization
        val downloadActions = Flowable
                .fromCallable(this::performAllWorkerActions)
                .onErrorReturnItem(Unit)
                .subscribeOn(Schedulers.io())

        runOnUiThread {
            LiveDataReactiveStreams
                    .fromPublisher(downloadActions)
                    .observe(this) { openMainActivityIfInitializationFinished() }
        }

        // Start background service and ensure cards are set
        sendBroadcast(Intent(this, StartSyncReceiver::class.java))

        // Request permissions for Android 6 and up
        requestLocationPermission()
    }

    private fun performAllWorkerActions() {
        for (action in workerActions.actions) {
            action.execute(CacheControl.USE_CACHE)
        }
    }

    private fun requestLocationPermission() {
        when {
            hasLocationPermissions -> requestNotificationPermission()
            shouldShowLocationRationale -> runOnUiThread { showLocationPermissionRationaleDialog() }
            else -> requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION)
        }
    }

    private val hasLocationPermissions: Boolean
        get() = PERMISSIONS_LOCATION.all { checkSelfPermission(this, it) == PERMISSION_GRANTED }

    private val shouldShowLocationRationale: Boolean
        get() = PERMISSIONS_LOCATION.all { shouldShowRequestPermissionRationale(this, it) }

    /**
     * Displays a dialog to the user explaining why we need the location permissions
     */
    private fun showLocationPermissionRationaleDialog() {
        AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog)
                .setMessage(R.string.permission_location_explanation)
                .setPositiveButton(R.string.ok) { _, _ ->
                    requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION)
                }
                .create()
                .apply {
                    window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
                }
                .show()
    }

    private fun requestNotificationPermission() {
        // Android 12 and below don't require this permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            openMainActivityIfInitializationFinished()
            return
        }
        when {
            hasNotificationPermission -> openMainActivityIfInitializationFinished()
            shouldShowRequestPermissionRationale(this, POST_NOTIFICATIONS) ->
                runOnUiThread { showNotificationPermissionRationaleDialog() }
            else -> requestPermissions(this, arrayOf(POST_NOTIFICATIONS), REQUEST_NOTIFICATION)
        }
    }

    private val hasNotificationPermission: Boolean
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        get() = checkSelfPermission(this, POST_NOTIFICATIONS) == PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showNotificationPermissionRationaleDialog() {
        AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog)
            .setMessage(R.string.permission_notification_explanation)
            .setPositiveButton(R.string.ok) { _, _ ->
                requestPermissions(this, arrayOf(POST_NOTIFICATIONS), REQUEST_NOTIFICATION)
            }
            .create()
            .apply {
                window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION) {
            // Request permission for Android 13 and up after location permission request
            requestNotificationPermission()
            return
        }

        openMainActivityIfInitializationFinished()
    }

    private fun openMainActivityIfInitializationFinished() {
        if (initializationFinished.compareAndSet(false, true) || isFinishing) {
            // If the initialization process is not yet finished or if the Activity is
            // already being finished, there's no need to open MainActivity.
            return
        }
        openMainActivity()
    }

    private fun openMainActivity() {
        val intent = Intent(this, BaseNavigationActivity::class.java)
        // If the app has been restarted after a language change,
        // the intent includes an extra which is passed on to BaseNavigationActivity to open up the settings again.
        if (getIntent().getBooleanExtra(Const.SETTINGS_RESTART, false)) {
            intent.putExtra(Const.SETTINGS_RESTART, true)
        }
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }

    private val ApplicationInfo.isDebuggable: Boolean
        get() = 0 != (flags and ApplicationInfo.FLAG_DEBUGGABLE)

    private companion object {
        private const val REQUEST_LOCATION = 0
        private const val REQUEST_NOTIFICATION = 1
        private val PERMISSIONS_LOCATION = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    }
}
