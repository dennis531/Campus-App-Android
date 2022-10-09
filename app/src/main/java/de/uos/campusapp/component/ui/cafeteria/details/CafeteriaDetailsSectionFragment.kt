package de.uos.campusapp.component.ui.cafeteria.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import de.uos.campusapp.databinding.FragmentCafeteriadetailsSectionBinding
import de.uos.campusapp.di.ViewModelFactory
import de.uos.campusapp.di.injector
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import javax.inject.Inject
import javax.inject.Provider

/**
 * Fragment for each cafeteria-page.
 */
class CafeteriaDetailsSectionFragment : Fragment() {

    @Inject
    internal lateinit var viewModelProvider: Provider<CafeteriaViewModel>

    private val cafeteriaViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(viewModelProvider)).get(CafeteriaViewModel::class.java)
    }

    private val binding by viewBinding(FragmentCafeteriadetailsSectionBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.cafeteriaComponent()
                .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_cafeteriadetails_section, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuDate = arguments?.getSerializable(Const.DATE) as DateTime
        val menuDateString = DateTimeFormat.fullDate().print(menuDate)
        val cafeteriaId = arguments?.getString(Const.CAFETERIA_ID)
        if (cafeteriaId == null) {
            Utils.log("Argument missing: cafeteriaId")
            return
        }

        with(binding) {
            menuDateTextView.text = menuDateString

            val hours = OpenHoursHelper(requireContext()).getHoursByIdAsString(cafeteriaId.toString())
            menuOpeningHours.text = hours
            menuOpeningHours.isVisible = hours.isNotEmpty()

            menusRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            menusRecyclerView.itemAnimator = DefaultItemAnimator()

            val adapter = CafeteriaMenusAdapter(requireContext(), true, null)
            menusRecyclerView.adapter = adapter

            cafeteriaViewModel.cafeteriaMenus.observe(viewLifecycleOwner, Observer<List<CafeteriaMenuItem>> { adapter.update(it) })
            cafeteriaViewModel.fetchCafeteriaMenus(cafeteriaId, menuDate)
        }
    }

    companion object {

        fun newInstance(cafeteriaId: String, dateTime: DateTime): CafeteriaDetailsSectionFragment {
            val fragment = CafeteriaDetailsSectionFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(Const.DATE, dateTime)
                putString(Const.CAFETERIA_ID, cafeteriaId)
            }
            return fragment
        }
    }
}
