package de.tum.`in`.tumcampusapp

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import de.tum.`in`.tumcampusapp.component.notifications.NotificationUtils.setupNotificationChannels
import de.tum.`in`.tumcampusapp.component.other.settings.ThemeProvider
import de.tum.`in`.tumcampusapp.component.tumui.calendar.CalendarActivity
import de.tum.`in`.tumcampusapp.component.tumui.calendar.widget.TimetableWidget
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.activity.CafeteriaActivity
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.widget.MensaWidget
import de.tum.`in`.tumcampusapp.component.ui.transportation.widget.TransportationWidget
import de.tum.`in`.tumcampusapp.di.AppComponent
import de.tum.`in`.tumcampusapp.di.DaggerAppComponent
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import io.reactivex.plugins.RxJavaPlugins
import net.danlew.android.joda.JodaTimeAndroid

open class App : Application() {

    // If we run Robolectric unit tests, this variable will be true
    open val testing = false

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        buildAppComponent()
        setupPicasso()
        setupNotificationChannels(this)
        JodaTimeAndroid.init(this)
        if (!testing) {
            initRxJavaErrorHandler()
        }
        setupStrictMode()
        loadTheme()
        //BackendHelper.getBackendConnection()
        setupWidgets()
        setupShortcuts()
    }

    private fun buildAppComponent() {
        // We use Dagger 2 for dependency injection. The main AppModule and AppComponent can be
        // found in the package "di".
        appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()
    }

    protected open fun setupPicasso() {
        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Integer.MAX_VALUE.toLong()))

        val built = builder.build()
        built.isLoggingEnabled = true

        Picasso.setSingletonInstance(built)
    }

    protected fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .permitDiskReads() // These are mainly caused by shared preferences and room. Probably enable
                    .permitDiskWrites() // this as soon as we don't call allowMainThreadQueries() in TcaDb
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()
                    // .detectLeakedClosableObjects() // seems like room / DAOs leak
                    .detectLeakedRegistrationObjects()
                    .detectFileUriExposure()
                    // .detectCleartextNetwork() // not available at the current minSdk
                    // .detectContentUriWithoutPermission()
                    // .detectUntaggedSockets()
                    .penaltyLog()
                    .build()
            )
        }
    }

    private fun initRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(FirebaseCrashlytics.getInstance()::recordException)
    }

    private fun loadTheme() {
        val theme = ThemeProvider(this).getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    private fun setupWidgets() {
        // Calendar Widget
        if (ConfigUtils.isComponentEnabled(this, Component.CALENDAR)) {
            packageManager.setComponentEnabledSetting(
                ComponentName(this, TimetableWidget::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        // Transportation Widget
        if (ConfigUtils.isComponentEnabled(this, Component.TRANSPORTATION)) {
            packageManager.setComponentEnabledSetting(
                ComponentName(this, TransportationWidget::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        // Cafeteria Widget
        if (ConfigUtils.isComponentEnabled(this, Component.CAFETERIA)) {
            packageManager.setComponentEnabledSetting(
                ComponentName(this, MensaWidget::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }

    private fun setupShortcuts() {
        val shortcuts = mutableListOf<ShortcutInfoCompat>()

        // Calendar Shortcut
        if (ConfigUtils.isComponentEnabled(this, Component.CALENDAR)) {
            shortcuts.add(
                ShortcutInfoCompat.Builder(this, "calendar")
                    .setShortLabel(getString(R.string.calendar))
                    .setIcon(IconCompat.createWithResource(this, R.drawable.ic_calendar_shortcut))
                    .setIntent(Intent(this, CalendarActivity::class.java).apply { action = Intent.ACTION_VIEW })
                    .build()
            )
        }

        // Cafeteria Shortcut
        if (ConfigUtils.isComponentEnabled(this, Component.CAFETERIA)) {
            shortcuts.add(
                ShortcutInfoCompat.Builder(this, "mensa")
                    .setShortLabel(getString(R.string.cafeteria))
                    .setIcon(IconCompat.createWithResource(this, R.drawable.ic_cafeteria_shortcut))
                    .setIntent(Intent(this, CafeteriaActivity::class.java).apply { action = Intent.ACTION_VIEW })
                    .build()
            )
        }

        ShortcutManagerCompat.setDynamicShortcuts(this, shortcuts)
    }
}
