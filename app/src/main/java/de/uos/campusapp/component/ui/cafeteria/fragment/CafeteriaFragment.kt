package de.uos.campusapp.component.ui.cafeteria.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.fragment.FragmentForDownloadingExternal
import de.uos.campusapp.component.other.locations.LocationManager
import de.uos.campusapp.component.ui.cafeteria.activity.CafeteriaNotificationSettingsActivity
import de.uos.campusapp.component.ui.cafeteria.controller.CafeteriaManager
import de.uos.campusapp.component.ui.cafeteria.details.CafeteriaDetailsSectionsPagerAdapter
import de.uos.campusapp.component.ui.cafeteria.details.CafeteriaViewModel
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaItem
import de.uos.campusapp.databinding.FragmentCafeteriaBinding
import de.uos.campusapp.di.ViewModelFactory
import de.uos.campusapp.di.injector
import de.uos.campusapp.service.DownloadWorker
import de.uos.campusapp.utils.*
import org.joda.time.DateTime
import javax.inject.Inject
import javax.inject.Provider

class CafeteriaFragment : FragmentForDownloadingExternal(
    R.layout.fragment_cafeteria,
    R.string.cafeteria
), AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var viewModelProvider: Provider<CafeteriaViewModel>

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var cafeteriaManager: CafeteriaManager

    @Inject
    lateinit var cafeteriaDownloadAction: DownloadWorker.Action

    private val ingredientsMessage: Int? by lazy {
        ConfigUtils.getConfig<Int?>(ConfigConst.CAFETERIA_INGREDIENTS_TEXT, null)
    }

    private var cafeterias = mutableListOf<CafeteriaItem>()

    private val cafeteriaViewModel: CafeteriaViewModel by lazy {
        val factory = ViewModelFactory(viewModelProvider)
        ViewModelProvider(this, factory).get(CafeteriaViewModel::class.java)
    }

    private val adapter: ArrayAdapter<CafeteriaItem> by lazy { createArrayAdapter() }
    private val sectionsPagerAdapter: CafeteriaDetailsSectionsPagerAdapter by lazy {
        CafeteriaDetailsSectionsPagerAdapter(childFragmentManager)
    }

    override val method: DownloadWorker.Action?
        get() = cafeteriaDownloadAction

    private val binding by viewBinding(FragmentCafeteriaBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.cafeteriaComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pager.offscreenPageLimit = 50

        binding.spinnerToolbar.adapter = adapter
        binding.spinnerToolbar.onItemSelectedListener = this

        cafeteriaViewModel.cafeterias.observeNonNull(this) { updateCafeterias(it) }
        cafeteriaViewModel.selectedCafeteria.observeNonNull(this) { onNewCafeteriaSelected(it) }
        cafeteriaViewModel.menuDates.observeNonNull(this) { updateSectionsPagerAdapter(it) }
        // TODO: ADD Price Category

        cafeteriaViewModel.error.observeNonNull(this) { isError ->
            if (isError) {
                showError(R.string.error_something_wrong)
            } else {
                showContentLayout()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val location = locationManager.getCurrentOrNextLocation()
        cafeteriaViewModel.fetchCafeterias(location)
    }

    private fun updateCafeterias(newCafeterias: List<CafeteriaItem>) {
        cafeterias.clear()
        cafeterias.addAll(newCafeterias)

        adapter.clear()
        adapter.addAll(newCafeterias)
        adapter.notifyDataSetChanged()
        initCafeteriaSpinner()
    }

    private fun onNewCafeteriaSelected(cafeteria: CafeteriaItem) {
        sectionsPagerAdapter.setCafeteriaId(cafeteria.id)
        cafeteriaViewModel.fetchMenuDates()
    }

    private fun initCafeteriaSpinner() {
        val intent = requireActivity().intent
        val cafeteriaId: String

        if (intent != null && intent.hasExtra(Const.MENSA_FOR_FAVORITEDISH)) {
            cafeteriaId = intent.getStringExtra(Const.MENSA_FOR_FAVORITEDISH) ?: NONE_SELECTED.toString()
            intent.removeExtra(Const.MENSA_FOR_FAVORITEDISH)
        } else if (intent != null && intent.hasExtra(Const.CAFETERIA_ID)) {
            cafeteriaId = intent.getStringExtra(Const.CAFETERIA_ID) ?: "0"
        } else {
            // If we're not provided with a cafeteria ID, we choose the best matching cafeteria.
            cafeteriaId = cafeteriaManager.bestMatchMensaId
        }

        updateCafeteriaSpinner(cafeteriaId)
    }

    private fun updateCafeteriaSpinner(cafeteriaId: String) {
        var selectedIndex = NONE_SELECTED

        for (cafeteria in cafeterias) {
            val index = cafeterias.indexOf(cafeteria)
            if (cafeteriaId == NONE_SELECTED.toString() || cafeteriaId == cafeteria.id) {
                selectedIndex = index
                break
            }
        }

        if (selectedIndex != NONE_SELECTED) {
            binding.spinnerToolbar.setSelection(selectedIndex)
        }
    }

    private fun updateSectionsPagerAdapter(menuDates: List<DateTime>) {
        with(binding) {
            pager.adapter = null
            sectionsPagerAdapter.update(menuDates)
            pager.adapter = sectionsPagerAdapter
        }
    }

    private fun createArrayAdapter(): ArrayAdapter<CafeteriaItem> {
        return object : ArrayAdapter<CafeteriaItem>(
            requireContext(), R.layout.simple_spinner_item_actionbar) {
            private val inflater = LayoutInflater.from(context)

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = inflater.inflate(
                    R.layout.simple_spinner_dropdown_item_actionbar_two_line, parent, false)
                val cafeteria = getItem(position)

                val name = v.findViewById<TextView>(android.R.id.text1)
                val address = v.findViewById<TextView>(android.R.id.text2)
                val distance = v.findViewById<TextView>(R.id.distance)

                if (cafeteria != null) {
                    name.text = cafeteria.name
                    address.text = cafeteria.address
                    address.isVisible = !cafeteria.address.isNullOrEmpty()
                    if (cafeteria.distance != null) {
                        distance.text = Utils.formatDistance(cafeteria.distance!!)
                    } else {
                        distance.visibility = View.GONE
                    }
                }

                return v
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val selected = cafeterias[position]
        cafeteriaViewModel.updateSelectedCafeteria(selected)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) = Unit

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_section_fragment_cafeteria_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val menuItemIngredients = menu.findItem(R.id.action_ingredients)

        // Only show ingredients message if text is configured
        menuItemIngredients.isVisible = ingredientsMessage != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ingredients -> {
                showIngredientsInfo()
                true
            }
            R.id.action_settings -> {
                openNotificationSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openNotificationSettings() {
        val intent = Intent(requireContext(), CafeteriaNotificationSettingsActivity::class.java)
        startActivity(intent)
    }

    private fun showIngredientsInfo() {
        // Build a alert dialog containing the mapping of ingredients to the numbers
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.action_ingredients)
            .setMessage(ingredientsMessage ?: R.string.cafeteria_no_ingredients)
            .setPositiveButton(R.string.ok, null)
            .create()
            .apply {
                window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
            }
            .show()
    }

    companion object {

        private const val NONE_SELECTED = -1

        @JvmStatic
        fun newInstance() = CafeteriaFragment()
    }
}
