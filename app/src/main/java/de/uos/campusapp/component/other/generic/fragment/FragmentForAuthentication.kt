package de.uos.campusapp.component.other.generic.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.onboarding.OnboardingActivity
import de.uos.campusapp.component.ui.onboarding.OnboardingNavigator

/**
 * This class provides several functions for authentication fragments in the onboarding component
 *
 * Specialise this fragment for new authentication methods
 */
abstract class FragmentForAuthentication<T>(
    @LayoutRes layoutId: Int,
    @StringRes titleResId: Int,
) : BaseFragment<T>(layoutId, titleResId) {

    private val navigator: OnboardingNavigator by lazy {
        OnboardingNavigator(requireActivity() as OnboardingActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCustomCloseIcon()
    }

    private fun setCustomCloseIcon() {
        val closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear)
        if (closeIcon != null) {
            val color = ContextCompat.getColor(requireContext(), R.color.color_primary)
            closeIcon.setTint(color)
        }

        requireActivity().findViewById<Toolbar?>(R.id.toolbar).run {
            navigationIcon = closeIcon
            setNavigationOnClickListener { requireActivity().finish() }
        }
    }

    /**
     * Call this function to navigate to the next Onboarding fragment
     */
    protected fun openNextOnboardingStep() {
        navigator.openNext()
    }
}
