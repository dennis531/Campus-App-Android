package de.uos.campusapp.component.other.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceFragmentCompat.OnPreferenceStartScreenCallback
import androidx.preference.PreferenceScreen
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.utils.Const

class SettingsActivity : BaseActivity(R.layout.activity_user_preferences),
    OnPreferenceStartScreenCallback {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val rootKey = intent.getStringExtra(Const.PREFERENCE_SCREEN)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_frame, SettingsFragment.newInstance(rootKey))
                .commit()
        }
    }

    override fun onPreferenceStartScreen(
        preferenceFragment: PreferenceFragmentCompat,
        preferenceScreen: PreferenceScreen
    ): Boolean {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_frame, SettingsFragment.newInstance(preferenceScreen.key))
            .addToBackStack(null)
            .commit()

        return true
    }

    // restart app after language change
    fun restartApp() {
        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.putExtra(Const.SETTINGS_RESTART, true)
        startActivity(i)
        finishAffinity()
    }

    companion object {

        fun newIntent(
            context: Context,
            rootKey: String
        ) = Intent(context, SettingsActivity::class.java).apply {
            putExtra(Const.PREFERENCE_SCREEN, rootKey)
        }
    }
}