package de.tum.`in`.tumcampusapp.component.ui.onboarding

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.auth.AuthManager
import de.tum.`in`.tumcampusapp.component.ui.onboarding.di.OnboardingScope
import de.tum.`in`.tumcampusapp.config.AuthMethod
import de.tum.`in`.tumcampusapp.config.Config
import de.tum.`in`.tumcampusapp.utils.*
import javax.inject.Inject

@OnboardingScope
class OnboardingNavigator @Inject constructor(
    private val activity: OnboardingActivity
) {
    @Inject
    lateinit var authManager: AuthManager

    private var didFinishFlow = false

    private val fragmentManager: FragmentManager
        get() = activity.supportFragmentManager

    fun openFirst() {
        val first = when (ConfigUtils.getConfig(ConfigConst.AUTH_METHOD, AuthMethod.OAUTH10A)) {
            // Add more authentication methods here
            AuthMethod.OAUTH10A -> OnboardingOAuth10aFragment.newInstance()
            else -> throw IllegalStateException("Authentication method not known.")
        }

        fragmentManager.beginTransaction()
            .replace(R.id.contentFrame, first)
            .commit()
    }

    fun openNext(destination: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.contentFrame, destination)
            .addToBackStack(null)
            .commit()
    }

    fun finish() {
        didFinishFlow = true
        val intent = Intent(activity, StartupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finishAndRemoveTask()
    }

    fun onClose() {
        if (!didFinishFlow) {
            // The user opened the onboarding screen and maybe filled out some information, but did
            // not finish it completely.
            authManager.clear()
        }
    }
}
