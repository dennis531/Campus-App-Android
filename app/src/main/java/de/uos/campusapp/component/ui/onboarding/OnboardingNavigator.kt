package de.uos.campusapp.component.ui.onboarding

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import de.uos.campusapp.R
import de.uos.campusapp.api.auth.AuthManager
import de.uos.campusapp.component.ui.onboarding.di.OnboardingScope
import de.uos.campusapp.config.AuthMethod
import de.uos.campusapp.utils.*
import javax.inject.Inject

@OnboardingScope
class OnboardingNavigator @Inject constructor(
    private val activity: OnboardingActivity
) {

    private val authManagers: List<AuthManager> by lazy {
        ConfigUtils.getAuthManagers(activity)
    }

    private val authMethods: List<AuthMethod> by lazy {
        ConfigUtils.getAuthMethods()
    }

    private var didFinishFlow = false

    private val fragmentManager: FragmentManager
        get() = activity.supportFragmentManager

    fun openFirst() {
        currentMethodIndex = INITIAL_INDEX

        val first = getNextFragment()

        fragmentManager.beginTransaction()
            .replace(R.id.contentFrame, first)
            .commit()
    }

    fun openNext() {
        openFragment(getNextFragment())
    }

    /**
     * Get the next authentication method and increments the current index
     */
    private fun getNextMethod(): AuthMethod? {
        return authMethods.getOrNull(++currentMethodIndex)
    }

    /**
     * Gets the next fragment
     */
    private fun getNextFragment(): Fragment {
        // Return extras fragment if no next method exists
        val authMethod = getNextMethod() ?: return OnboardingExtrasFragment.newInstance()

        return when (authMethod) {
            // Add more authentication methods here
            AuthMethod.OAUTH10A -> OnboardingOAuth10aFragment.newInstance()
            else -> throw IllegalStateException("Authentication method not known.")
        }
    }

    fun openFragment(destination: Fragment) {
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
            authManagers.forEach { it.clear() }
        }
    }

    companion object {
        private const val INITIAL_INDEX = -1

        @JvmStatic
        private var currentMethodIndex: Int = INITIAL_INDEX
    }
}
