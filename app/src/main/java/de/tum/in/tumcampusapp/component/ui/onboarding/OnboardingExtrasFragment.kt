package de.tum.`in`.tumcampusapp.component.ui.onboarding

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import com.google.gson.Gson
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.auth.AuthManager
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.component.other.generic.fragment.FragmentForLoadingInBackground
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatRoomController
import de.tum.`in`.tumcampusapp.component.ui.chat.api.ChatAPI
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatMember
import de.tum.`in`.tumcampusapp.component.ui.onboarding.di.OnboardingComponent
import de.tum.`in`.tumcampusapp.component.ui.onboarding.di.OnboardingComponentProvider
import de.tum.`in`.tumcampusapp.databinding.FragmentOnboardingExtrasBinding
import de.tum.`in`.tumcampusapp.service.SilenceService
import de.tum.`in`.tumcampusapp.utils.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.support.v4.browse
import javax.inject.Inject

class OnboardingExtrasFragment : FragmentForLoadingInBackground<ChatMember>(
    R.layout.fragment_onboarding_extras,
    R.string.connect_to_tum_online
) {

    private val onboardingComponent: OnboardingComponent by lazy {
        (requireActivity() as OnboardingComponentProvider).onboardingComponent()
    }

    @Inject
    lateinit var cacheManager: CacheManager

    @Inject
    lateinit var apiClient: LMSClient

    @Inject
    lateinit var chatRoomController: ChatRoomController

    @Inject
    lateinit var authManager: AuthManager

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

        if (authManager.hasAccess()) {
            cacheManager.fillCache()
        }

        with(binding) {
//            bugReportsCheckBox.isChecked = Utils.getSettingBool(requireContext(), Const.BUG_REPORTS, true)

            privacyPolicyButton.setOnClickListener { browse(getString(R.string.url_privacy_policy)) }
            finishButton.setOnClickListener { startLoading() }
        }

    }

    private fun setupSilentModeView() {
        with(binding) {
            if (!ConfigUtils.isComponentEnabled(requireContext(), Component.CALENDAR)) {
                silentModeCheckBox.visibility = View.GONE
                silentModeTextView.visibility = View.GONE
                return
            }

            if (authManager.hasAccess()) {
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

            if (authManager.hasAccess()) {
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
            Const.CHAT_ROOM_DISPLAY_NAME, getString(R.string.not_connected_to_lms))

        val currentChatMember = ChatMember(userId, username, name)

        if (currentChatMember.username.isNullOrEmpty()) {
            return currentChatMember
        }

        // Try to restore already joined chat rooms from server
        return try {
            val rooms = (apiClient as ChatAPI).getChatRooms()
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

                    if (result!= null && !result.username.isNullOrEmpty()) {
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

    private fun SharedPreferences.Editor.put(key: String, value: Any) {
        putString(key, Gson().toJson(value))
    }

    companion object {
        fun newInstance() = OnboardingExtrasFragment()
    }
}
