package de.tum.`in`.tumcampusapp.component.ui.onboarding

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.auth.OAuthManager
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.component.other.generic.fragment.BaseFragment
import de.tum.`in`.tumcampusapp.component.tumui.person.model.PersonInterface
import de.tum.`in`.tumcampusapp.component.ui.onboarding.di.OnboardingComponent
import de.tum.`in`.tumcampusapp.component.ui.onboarding.di.OnboardingComponentProvider
import de.tum.`in`.tumcampusapp.databinding.FragmentOnboardingOauthBinding
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
import de.tum.`in`.tumcampusapp.utils.plusAssign
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import oauth.signpost.exception.OAuthCommunicationException
import oauth.signpost.exception.OAuthExpectationFailedException
import oauth.signpost.exception.OAuthMessageSignerException
import oauth.signpost.exception.OAuthNotAuthorizedException
import javax.inject.Inject

class OnboardingOAuth10aFragment : BaseFragment<String>(
    R.layout.fragment_onboarding_oauth,
    R.string.connect_to_your_campus
) {
    private val compositeDisposable = CompositeDisposable()

    private val onboardingComponent: OnboardingComponent by lazy {
        (requireActivity() as OnboardingComponentProvider).onboardingComponent()
    }

    @Inject
    lateinit var authManager: OAuthManager

    @Inject
    lateinit var apiClient: LMSClient

    @Inject
    lateinit var navigator: OnboardingNavigator

    private val binding by viewBinding(FragmentOnboardingOauthBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onboardingComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableRefresh()
        setCustomCloseIcon()

        binding.nextButton.setOnClickListener { onNextPressed() }
    }

    override fun onResume() {
        super.onResume()

        // OAuth callback
        Utils.log((requireActivity().intent.data == null).toString())
        requireActivity().intent?.data?.let {
            if (authManager.isCallbackUrl(it) && !authManager.hasAccess()) {
                showLoadingStart()
                compositeDisposable += Observable.fromCallable { authManager.retrieveAccessToken(it) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { loadIdentity() },
                        {
                            handleAuthenticationFailure(it)
                        }
                    )
            }
        }
    }

    private fun handleAuthenticationFailure(t: Throwable) {
        resetToken()
        displayErrorDialog(t)
    }

    /**
     * Display an obtrusive error dialog because on the provided [Throwable].
     * @param throwable The [Throwable] that occurred
     */
    private fun displayErrorDialog(throwable: Throwable) {
        val messageResId = when (throwable) {
            is OAuthMessageSignerException -> R.string.error_oauth_request_signing_failed
            is OAuthNotAuthorizedException -> R.string.error_oauth_not_authorized
            is OAuthExpectationFailedException -> R.string.error_unknown
            is OAuthCommunicationException -> R.string.error_oauth_communication_failed
            else -> R.string.error_unknown
        }

        AlertDialog.Builder(requireContext())
            .setMessage(messageResId)
            .setPositiveButton(R.string.ok, null)
            .setCancelable(true)
            .create()
            .apply {
                window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
            }
            .show()
    }

    private fun setCustomCloseIcon() {
        val closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear)
        if (closeIcon != null) {
            val color = ContextCompat.getColor(requireContext(), R.color.color_primary)
            closeIcon.setTint(color)
        }
        with(binding.toolbarBinding) {
            toolbar.navigationIcon = closeIcon
            toolbar.setNavigationOnClickListener { requireActivity().finish() }
        }

    }

    private fun onNextPressed() {
        if (authManager.hasAccess()) {
            AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.error_access_token_already_set_generate_new))
                .setPositiveButton(getString(R.string.generate_new_token)) { _, _ ->
                    generateNewToken()
                }
                .setNegativeButton(getString(R.string.use_existing)) { _, _ ->
                    openNextOnboardingStep()
                }
                .create()
                .apply {
                    window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
                }
                .show()
        } else {
            requestAuthenticationUrl()
        }
    }

    private fun requestAuthenticationUrl() {
        showLoadingStart()
        compositeDisposable += Observable.fromCallable { authManager.getAuthenticationUrl() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { openAuthenticationUrl(it) },
                { handleAuthenticationFailure(it) }
            )
    }

    private fun openAuthenticationUrl(result: String?) {
        showLoadingEnded()
        // Open the Authorization URI in a Custom Tab.
        val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(requireContext(), Uri.parse(result))
    }

    private fun generateNewToken() {
        resetToken()
        requestAuthenticationUrl()
    }

    private fun resetToken() {
        authManager.clear()
    }

    private fun loadIdentity() {
        compositeDisposable += Single.fromCallable { apiClient.getIdentity() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                handleIdentity(response)
            }, {
                handleAuthenticationFailure(it)
            })
    }


    private fun handleIdentity(person: PersonInterface) {
        // Collect personal data
        Utils.setSetting(requireContext(), Const.USERNAME, person.username)
        Utils.setSetting(requireContext(), Const.EMPLOYEE_MODE, false)

        Utils.setSetting(requireContext(), Const.PROFILE_ID, person.id)
        Utils.setSetting(requireContext(), Const.PROFILE_PICTURE_URL, person.imageUrl)
        Utils.setSetting(requireContext(), Const.PROFILE_EMAIL, person.email)

        Utils.setSetting(requireContext(), Const.CHAT_ROOM_DISPLAY_NAME, person.fullName)

        showLoadingEnded()
        openNextOnboardingStep()
    }

    private fun openNextOnboardingStep() {
        navigator.openNext(OnboardingExtrasFragment.newInstance())
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    companion object {
        fun newInstance() = OnboardingOAuth10aFragment()
    }
}