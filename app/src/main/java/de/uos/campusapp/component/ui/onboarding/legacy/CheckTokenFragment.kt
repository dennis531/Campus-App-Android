package de.uos.campusapp.component.ui.onboarding.legacy

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.api.tumonline.TUMOnlineClient
import de.uos.campusapp.api.general.exception.UnauthorizedException
import de.uos.campusapp.component.other.generic.fragment.BaseFragment
import de.uos.campusapp.component.ui.onboarding.OnboardingNavigator
//import de.tum.`in`.de.uos.de.uos.campusapp.component.ui.person.model.IdentitySet
import de.uos.campusapp.component.ui.onboarding.di.OnboardingComponent
import de.uos.campusapp.component.ui.onboarding.di.OnboardingComponentProvider
import de.uos.campusapp.databinding.FragmentCheckTokenBinding
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.support.v4.browse
import java.net.UnknownHostException
import javax.inject.Inject

//sealed class IdentityResponse {
//    data class Success(val identity: IdentitySet) : IdentityResponse()
//    data class Failure(val throwable: Throwable) : IdentityResponse()
//}

class CheckTokenFragment : BaseFragment<Unit>(
    R.layout.fragment_check_token,
    R.string.connect_to_your_campus
) {

    private val compositeDisposable = CompositeDisposable()

    private val onboardingComponent: OnboardingComponent by lazy {
        (requireActivity() as OnboardingComponentProvider).onboardingComponent()
    }

    private val tumOnlineClient: TUMOnlineClient by lazy {
        TUMOnlineClient.getInstance(requireContext())
    }

    @Inject
    lateinit var navigator: OnboardingNavigator

    private val binding by viewBinding(FragmentCheckTokenBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onboardingComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableRefresh()
        with(binding) {
            openTumOnlineButton.setOnClickListener { browse(Const.TUM_CAMPUS_URL) }
            nextButton.setOnClickListener { loadIdentitySet() }
        }

    }

    private fun loadIdentitySet() {
        val toast = Toast.makeText(requireContext(), R.string.checking_if_token_enabled, Toast.LENGTH_LONG)
        toast.show()

//        compositeDisposable += tumOnlineClient.getIdentity()
//            .map { IdentityResponse.Success(it) as IdentityResponse }
//            .doOnError(Utils::log)
//            .onErrorReturn { IdentityResponse.Failure(it) }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { response ->
//                toast.cancel()
//                when (response) {
//                    is IdentityResponse.Success -> handleDownloadSuccess(response.identity)
//                    is IdentityResponse.Failure -> handleDownloadFailure(response.throwable)
//                }
//            }
    }

//    private fun handleDownloadSuccess(identitySet: IdentitySet) {
//        val identity = identitySet.ids.first()
//        Utils.setSetting(requireContext(), Const.CHAT_ROOM_DISPLAY_NAME, identity.toString())
//
//        val ids = identity.obfuscated_ids
//
//        // Save the TUMonline ID to preferences
//        // Switch to identity.getObfuscated_id() in the future
//        Utils.setSetting(requireContext(), Const.TUMO_PIDENT_NR, ids.studierende)
//        Utils.setSetting(requireContext(), Const.TUMO_STUDENT_ID, ids.studierende)
//        Utils.setSetting(requireContext(), Const.TUMO_EMPLOYEE_ID, ids.bedienstete)
//        Utils.setSetting(requireContext(), Const.TUMO_EXTERNAL_ID, ids.extern) // usually alumni
//
//        if (ids.bedienstete.isNotEmpty() && ids.studierende.isEmpty() && ids.extern.isEmpty()) {
//            Utils.setSetting(requireContext(), Const.EMPLOYEE_MODE, true)
//            // only preset cafeteria prices if the user is only an employee
//            // since we can't determine which id is active (given once and never removed)
//            Utils.setSetting(requireContext(), Const.ROLE, "1")
//        }
//
//        // Note: we can't upload the obfuscated ids here since we might not have a (chat) member yet
//
//        navigator.openNext()
//    }

    private fun handleDownloadFailure(t: Throwable) {
        val messageResId = when (t) {
            is UnknownHostException -> R.string.no_internet_connection
            is UnauthorizedException -> R.string.error_access_token_inactive
            else -> R.string.error_unknown
        }

        Utils.showToast(requireContext(), messageResId)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    companion object {
        fun newInstance() = CheckTokenFragment()
    }
}
