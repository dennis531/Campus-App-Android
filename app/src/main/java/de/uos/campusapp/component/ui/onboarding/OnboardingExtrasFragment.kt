package de.uos.campusapp.component.ui.onboarding

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.google.gson.Gson
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.api.auth.AuthManager
import de.uos.campusapp.api.general.exception.ForbiddenException
import de.uos.campusapp.api.general.exception.NotFoundException
import de.uos.campusapp.api.general.exception.UnauthorizedException
import de.uos.campusapp.component.other.generic.fragment.FragmentForLoadingInBackground
import de.uos.campusapp.component.ui.chat.ChatRoomController
import de.uos.campusapp.component.ui.chat.api.ChatAPI
import de.uos.campusapp.component.ui.chat.model.ChatMember
import de.uos.campusapp.component.ui.onboarding.api.OnboardingAPI
import de.uos.campusapp.component.ui.onboarding.di.OnboardingComponent
import de.uos.campusapp.component.ui.onboarding.di.OnboardingComponentProvider
import de.uos.campusapp.component.ui.onboarding.model.IdentityInterface
import de.uos.campusapp.databinding.FragmentOnboardingExtrasBinding
import de.uos.campusapp.service.SilenceService
import de.uos.campusapp.utils.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.support.v4.browse
import java.net.UnknownHostException
import javax.inject.Inject

class OnboardingExtrasFragment : FragmentForLoadingInBackground<ChatMember>(
    R.layout.fragment_onboarding_extras,
    R.string.connect_to_your_campus
) {
    private val compositeDisposable = CompositeDisposable()

    private val onboardingComponent: OnboardingComponent by lazy {
        (requireActivity() as OnboardingComponentProvider).onboardingComponent()
    }

    private val calendarAuthManager: AuthManager by lazy {
        ConfigUtils.getAuthManager(requireContext(), Component.CALENDAR)
    }

    private val chatAuthManager: AuthManager by lazy {
        ConfigUtils.getAuthManager(requireContext(), Component.CHAT)
    }

    private val chatApiClient: ChatAPI by lazy {
        ConfigUtils.getApiClient(requireContext(), Component.CHAT) as ChatAPI
    }

    @Inject
    lateinit var onboardingApiClient: OnboardingAPI

    @Inject
    lateinit var cacheManager: CacheManager

    @Inject
    lateinit var chatRoomController: ChatRoomController

    @Inject
    lateinit var navigator: OnboardingNavigator

    private val binding by viewBinding(FragmentOnboardingExtrasBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onboardingComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSilentModeView()
        setupGroupChatView()

        with(binding) {
//            bugReportsCheckBox.isChecked = Utils.getSettingBool(requireContext(), Const.BUG_REPORTS, true)

            privacyPolicyButton.setOnClickListener { browse(getString(R.string.url_privacy_policy)) }
            // Reactivate button after identity is loaded successfully
            finishButton.isEnabled = false
            finishButton.setOnClickListener { startLoading() }
        }

        loadIdentity()
    }

    // Move identity loading to extras fragment
    private fun loadIdentity() {
        showLoadingStart()
        compositeDisposable += Single.fromCallable { onboardingApiClient.getIdentity() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                handleIdentity(response)
                cacheManager.fillCache()
            }, {
                handleIdentityFailure(it)
            })
    }

    private fun handleIdentity(identity: IdentityInterface) {
        AuthManager.saveIdentity(requireContext(), identity)

        showLoadingEnded()
        binding.finishButton.isEnabled = true
    }

    private fun handleIdentityFailure(t: Throwable) {
        displayErrorDialog(t)
    }

    /**
     * Display an obtrusive error dialog because on the provided [Throwable].
     * @param throwable The [Throwable] that occurred
     */
    private fun displayErrorDialog(throwable: Throwable) {
        val messageResId = when (throwable) {
            is UnknownHostException -> R.string.no_internet_connection
            is UnauthorizedException -> R.string.error_unauthorized
            is ForbiddenException -> R.string.error_no_rights_to_access_function
            is NotFoundException -> R.string.error_resource_not_found
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

    private fun setupSilentModeView() {
        with(binding) {
            if (!ConfigUtils.isComponentEnabled(requireContext(), Component.CALENDAR, false)) {
                silentModeCheckBox.visibility = View.GONE
                silentModeTextView.visibility = View.GONE
                return
            }

            if (calendarAuthManager.hasAccess()) {
                silentModeCheckBox.isChecked =
                    Utils.getSettingBool(requireContext(), Const.SILENCE_SERVICE, false)
                silentModeCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked && !SilenceService.hasPermissions(requireContext())) {
                        SilenceService.requestPermissions(requireContext())
                        silentModeCheckBox.isChecked = false
                    }
                }
            } else {
                silentModeCheckBox.isChecked = false
                silentModeCheckBox.isEnabled = false
            }
        }
    }

    private fun setupGroupChatView() {
        with(binding) {
            if (!ConfigUtils.isComponentEnabled(requireContext(), Component.CHAT)) {
                groupChatCheckBox.visibility = View.GONE
                groupChatTextView.visibility = View.GONE
                return
            }

            if (chatAuthManager.hasAccess()) {
                groupChatCheckBox.isChecked =
                    Utils.getSettingBool(requireContext(), Const.GROUP_CHAT_ENABLED, true)
            } else {
                groupChatCheckBox.isChecked = false
                groupChatCheckBox.isEnabled = false
            }
        }
    }

    override fun onLoadInBackground(): ChatMember? {
        if (!NetUtils.isConnected(requireContext())) {
            showNoInternetLayout()
            return null
        }

        // By now, the user should be authenticated to the lms

        if (!ConfigUtils.isComponentEnabled(requireContext(), Component.CHAT)) {
            return ChatMember()
        }

        // Create chat member
        val userId = Utils.getSetting(requireContext(), Const.PROFILE_ID, "")
        val username = Utils.getSetting(requireContext(), Const.USERNAME, "")
        val name = Utils.getSetting(requireContext(),
            Const.PROFILE_DISPLAY_NAME, getString(R.string.not_connected_to_api))

        val currentChatMember = ChatMember(userId, username, name)

        if (currentChatMember.username.isNullOrEmpty()) {
            return currentChatMember
        }

        // Try to restore already joined chat rooms from server
        return try {
            val rooms = chatApiClient.getChatRooms()
            chatRoomController.replaceIntoRooms(rooms)

            currentChatMember
        } catch (t: Throwable) {
            Utils.log(t)
            null
        }
    }

    override fun onLoadFinished(result: ChatMember?) {
        // Gets the editor for editing preferences and updates the preference values with the
        // chosen state
        requireContext()
            .defaultSharedPreferences
            .edit {
                with(binding) {
                    putBoolean(Const.SILENCE_SERVICE, silentModeCheckBox.isChecked)
                    putBoolean(Const.BACKGROUND_MODE, true) // Enable by default
//                    putBoolean(Const.BUG_REPORTS, bugReportsCheckBox.isChecked)

                    if (result != null && !result.username.isNullOrEmpty()) {
                        putBoolean(Const.GROUP_CHAT_ENABLED, groupChatCheckBox.isChecked)
                        put(Const.CHAT_MEMBER, result)
                    }
                }
            }

        finishOnboarding()
    }

    private fun finishOnboarding() {
        navigator.finish()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun SharedPreferences.Editor.put(key: String, value: Any) {
        putString(key, Gson().toJson(value))
    }

    companion object {
        fun newInstance() = OnboardingExtrasFragment()
    }
}
