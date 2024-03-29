package de.uos.campusapp.component.ui.overview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.pm.PackageInfoCompat
import de.psdev.licensesdialog.LicensesDialog
import de.uos.campusapp.BuildConfig
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.databinding.ActivityInformationBinding
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.Const

/**
 * Provides information about this app and all contributors
 */
class InformationActivity : BaseActivity(R.layout.activity_information, Component.OVERVIEW) {

    private lateinit var binding: ActivityInformationBinding
    private val rowParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        binding = ActivityInformationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFacebook.setOnClickListener {
            openFacebook()
        }
        binding.buttonGithub.setOnClickListener {
            startActivity(Intent(ACTION_VIEW, Uri.parse(getString(R.string.github_link))))
        }
        binding.buttonPrivacy.setOnClickListener {
            startActivity(Intent(ACTION_VIEW, Uri.parse(getString(R.string.url_privacy_policy))))
        }
        binding.buttonLicenses.setOnClickListener {
            LicensesDialog.Builder(this)
                    .setNotices(R.raw.notices)
                    .setShowFullLicenseText(false)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show()
        }

        displayDebugInfo()
    }

    /**
     * Open the Facebook app or view page in a browser if Facebook is not installed.
     */
    private fun openFacebook() {
        try {
            packageManager.getPackageInfo("com.facebook.katana", 0)
            val intent = Intent(ACTION_VIEW, Uri.parse(getString(R.string.facebook_link_app)))
            startActivity(intent)
        } catch (e: Exception) {
            // Don't make any assumptions about another app, just start the browser instead
            val default = Intent(ACTION_VIEW, Uri.parse(getString(R.string.facebook_link)))
            startActivity(default)
        }
    }

    private fun displayDebugInfo() {
        // Setup showing of debug information
        val sp = PreferenceManager.getDefaultSharedPreferences(this)

        with(binding) {
            try {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                addDebugRow(debugInfos, "App version", packageInfo.versionName)
            } catch (ignore: NameNotFoundException) {
            }

            addDebugRow(debugInfos, "Username", sp.getString(Const.USERNAME, ""))
            val token = sp.getString(Const.ACCESS_TOKEN, "")
            if (token == "") {
                addDebugRow(debugInfos, "Access token", "")
            } else {
                addDebugRow(debugInfos, "Access token", token?.substring(0, 5) + "...")
            }
            addDebugRow(debugInfos, "Bug reports", sp.getBoolean(Const.BUG_REPORTS, false).toString() + " ")

            try {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                addDebugRow(debugInfos, "Version code", PackageInfoCompat.getLongVersionCode(packageInfo).toString())
            } catch (ignore: NameNotFoundException) {
            }

            addDebugRow(debugInfos, "Build configuration", BuildConfig.DEBUG.toString())
            debugInfos.visibility = View.VISIBLE
        }
    }

    private fun addDebugRow(tableLayout: TableLayout, label: String, value: String?) {
        val tableRow = TableRow(this)
        tableRow.layoutParams = rowParams

        val labelTextView = TextView(this).apply {
            text = label
            layoutParams = rowParams

            val padding = resources.getDimensionPixelSize(R.dimen.material_small_padding)
            setPadding(0, 0, padding, 0)
        }
        tableRow.addView(labelTextView)

        val valueTextView = TextView(this).apply {
            text = value
            layoutParams = rowParams
            isClickable = true

            setOnLongClickListener {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label, value)
                clipboard.setPrimaryClip(clip)
                true
            }
        }
        tableRow.addView(valueTextView)

        tableLayout.addView(tableRow)
    }
}
