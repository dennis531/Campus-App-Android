package de.uos.campusapp.component.ui.person

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.component.other.general.RecentsDao
import de.uos.campusapp.component.other.general.model.Recent
import de.uos.campusapp.component.other.generic.fragment.FragmentForSearching
import de.uos.campusapp.component.ui.person.api.PersonAPI
import de.uos.campusapp.component.ui.person.model.PersonInterface
import de.uos.campusapp.component.ui.person.model.Person
import de.uos.campusapp.database.CaDb
import de.uos.campusapp.databinding.FragmentPersonSearchBinding
import de.uos.campusapp.di.injector
import javax.inject.Inject

class PersonSearchFragment : FragmentForSearching<List<PersonInterface>>(
    R.layout.fragment_person_search,
    R.string.person_search,
    PersonSearchSuggestionProvider.AUTHORITY,
    minLength = 3
) {
    @Inject
    lateinit var apiClient: PersonAPI

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
        recentsDao = CaDb.getInstance(requireContext()).recentsDao()

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
        fetch { apiClient.searchPerson(query) }
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
