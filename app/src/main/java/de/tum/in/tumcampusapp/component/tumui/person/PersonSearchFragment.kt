package de.tum.`in`.tumcampusapp.component.tumui.person

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.component.other.general.RecentsDao
import de.tum.`in`.tumcampusapp.component.other.general.model.Recent
import de.tum.`in`.tumcampusapp.component.other.generic.fragment.FragmentForSearching
import de.tum.`in`.tumcampusapp.component.tumui.calendar.api.CalendarAPI
import de.tum.`in`.tumcampusapp.component.tumui.person.api.PersonAPI
import de.tum.`in`.tumcampusapp.component.tumui.person.model.PersonInterface
import de.tum.`in`.tumcampusapp.component.tumui.person.model.Person
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.databinding.FragmentPersonSearchBinding
import de.tum.`in`.tumcampusapp.di.injector
import de.tum.`in`.tumcampusapp.utils.Utils
import io.reactivex.Single
import javax.inject.Inject

class PersonSearchFragment : FragmentForSearching<List<PersonInterface>>(
    R.layout.fragment_person_search,
    R.string.person_search,
    PersonSearchSuggestionProvider.AUTHORITY,
    minLength = 3
) {
    @Inject
    lateinit var apiClient: LMSClient

    private lateinit var recentsDao: RecentsDao

    private val recents: List<PersonInterface>
        get() {
            val recents = recentsDao.getAll(RecentsDao.PERSONS) ?: return emptyList()
            return recents.map { recent -> Person.fromRecent(recent) }
        }

    private val binding by viewBinding(FragmentPersonSearchBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.personComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recentsDao = TcaDb.getInstance(requireContext()).recentsDao()

        binding.personsRecyclerView.setHasFixedSize(true)
        disableRefresh()

        val adapter = PersonSearchResultsAdapter(recents, this::onItemClick)
        if (adapter.itemCount == 0) {
            openSearch()
        }
        binding.personsRecyclerView.adapter = adapter

        val itemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.personsRecyclerView.addItemDecoration(itemDecoration)
    }

    private fun onItemClick(person: PersonInterface) {
        val lastSearch = person.id + "$" + person.fullName.trim { it <= ' ' }
        recentsDao.insert(Recent(lastSearch, RecentsDao.PERSONS))
        showPersonDetails(person)
    }

    override fun onStartSearch() {
        with(binding) {
            recentsHeader.visibility = View.VISIBLE
            val adapter = personsRecyclerView.adapter as? PersonSearchResultsAdapter
            adapter?.update(recents)
        }
    }

    override fun onStartSearch(query: String?) {
        query?.let { searchPerson(it) }
    }

    private fun searchPerson(query: String) {
        if (apiClient !is PersonAPI) {
            showError(R.string.error_function_not_available)
            return
        }

        fetch { (apiClient as PersonAPI).searchPerson(query) }
    }

    override fun onDownloadSuccessful(response: List<PersonInterface>) {
        with(binding) {
            recentsHeader.visibility = View.GONE

            if (response.size == 1) {
                showPersonDetails(response.first())
            } else {
                val adapter = personsRecyclerView.adapter as? PersonSearchResultsAdapter
                adapter?.update(response)
            }
        }
    }

    private fun showPersonDetails(person: PersonInterface) {
        val intent = Intent(requireContext(), PersonDetailsActivity::class.java).apply {
            putExtra("personObject", person)
        }
        startActivity(intent)
    }

    companion object {
        fun newInstance() = PersonSearchFragment()
    }
}
