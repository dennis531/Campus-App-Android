package de.uos.campusapp.component.other.generic.activity

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import de.uos.campusapp.di.AppComponent
import de.uos.campusapp.di.app
import java.util.Locale
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import de.uos.campusapp.R
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import org.jetbrains.anko.contentView

abstract class BaseActivity(
    @LayoutRes private val layoutId: Int,
    val component: Component? = null
) : AppCompatActivity() {

    val injector: AppComponent by lazy { app.appComponent }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        component?.let {
            if (!ConfigUtils.isComponentEnabled(this, component)) {
                val componentTitle = component.getTitle(this)
                Toast.makeText(this, getString(R.string.component_not_available_format, componentTitle), Toast.LENGTH_LONG).show()
                this.finish()
            }
        }

        val view = onCreateView(layoutInflater, savedInstanceState)
        setContentView(view)

        onViewCreated(contentView!!, savedInstanceState)
    }

    open fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, null)
    }

    open fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        // TODO Refactor
        if (this !is BaseNavigationActivity) {
            setSupportActionBar(toolbar)

            supportActionBar?.let {
                val parent = NavUtils.getParentActivityName(this)
                if (parent != null) {
                    it.setDisplayHomeAsUpEnabled(true)
                    it.setHomeButtonEnabled(true)
                }
            }
        }

        initLanguage(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    // load language from user's preferences
    private fun initLanguage(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var lang = sharedPreferences.getString("language_preference", null)
        if (lang == null) {
            lang = Locale.getDefault().language
            val availableLangs = resources.getStringArray(R.array.language_values)
            if (!availableLangs.contains(lang)) lang = "en"

            val editor = sharedPreferences.edit()
            editor.putString("language_preference", lang)
            editor.apply()
        }
        val locale = Locale(lang)

        Locale.setDefault(locale)
        val config = baseContext.resources.configuration
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val activityInfo = packageManager.getActivityInfo(this.componentName, PackageManager.GET_META_DATA)
        if (activityInfo.labelRes != 0 && supportActionBar != null) {
            supportActionBar!!.setTitle(activityInfo.labelRes)
        }
    }
}
