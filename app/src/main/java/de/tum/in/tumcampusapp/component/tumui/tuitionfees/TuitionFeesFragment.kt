package de.tum.`in`.tumcampusapp.component.tumui.tuitionfees

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.component.other.generic.fragment.FragmentForAccessingApi
import de.tum.`in`.tumcampusapp.component.tumui.tuitionfees.model.AbstractTuition
import de.tum.`in`.tumcampusapp.databinding.FragmentTuitionFeesBinding
import de.tum.`in`.tumcampusapp.utils.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.Locale

class TuitionFeesFragment : FragmentForAccessingApi<AbstractTuition>(
    R.layout.fragment_tuition_fees,
    R.string.tuition_fees,
    Component.TUITIONFEES
) {

    private val tuitionFeeManager by lazy { TuitionFeeManager(requireContext()) }

    private val binding by viewBinding(FragmentTuitionFeesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tuitionFeesLink: String? = ConfigUtils.getConfig(ConfigConst.TUITIONFEES_LINK, null)

        binding.additionalInfoButton.isVisible = !tuitionFeesLink.isNullOrBlank()
        tuitionFeesLink?.let { url ->
            binding.additionalInfoButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }

        refreshData(CacheControl.USE_CACHE)
    }

    override fun onRefresh() {
        refreshData(CacheControl.BYPASS_CACHE)
    }

    private fun refreshData(cacheControl: CacheControl) {
        fetch { tuitionFeeManager.loadTuition(cacheControl) }
    }

    override fun onDownloadSuccessful(response: AbstractTuition) {
        showTuition(response)
    }

    override fun onEmptyDownloadResponse() {
        showTuitionNotAvailable()
    }

    private fun showTuition(tuition: AbstractTuition) {
        if (!tuition.hasStarted) {
            showTuitionNotAvailable()
            return
        }

        with(binding) {
            val amountText = tuition.getAmountText(requireContext())
            amountTextView.text = amountText

            val deadline = tuition.deadline
            val formatter = DateTimeFormat.longDate().withLocale(Locale.getDefault())
            val formattedDeadline = formatter.print(deadline)
            deadlineTextView.text = getString(R.string.due_on_format_string, formattedDeadline)

            semesterTextView.text = tuition.semester

            // show paid switch only if tuition is set in configs
            paidSwitch.isVisible = tuition.canMarkedAsPaid(requireContext())
            paidSwitch.isChecked = Utils.getSettingBool(requireContext(), Const.TUITION_PAID, false)
            paidSwitch.setOnCheckedChangeListener { _, isPaid ->
                Utils.setSetting(requireContext(), Const.TUITION_PAID, isPaid)
                refreshData(CacheControl.BYPASS_CACHE)
            }

            if (tuition.isPaid(requireContext())) {
                amountTextView.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.sections_green))
            } else {
                // check if the deadline is less than a week from now
                val nextWeek = DateTime().plusWeeks(1)
                if (nextWeek.isAfter(deadline)) {
                    amountTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.error))
                } else {
                    amountTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                }
            }
        }
    }

    private fun showTuitionNotAvailable() {
        showError(R.string.no_tuition_fees)
    }

    companion object {
        @JvmStatic fun newInstance() = TuitionFeesFragment()
    }
}
