package de.tum.`in`.tumcampusapp.component.tumui.lectures.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.NoResultsAdapter
import de.tum.`in`.tumcampusapp.component.other.generic.fragment.FragmentForSearching
import de.tum.`in`.tumcampusapp.component.tumui.lectures.LectureSearchSuggestionProvider
import de.tum.`in`.tumcampusapp.component.tumui.lectures.activity.LectureDetailsActivity
import de.tum.`in`.tumcampusapp.component.tumui.lectures.adapter.LecturesListAdapter
import de.tum.`in`.tumcampusapp.component.tumui.lectures.api.LecturesAPI
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.AbstractLecture
import de.tum.`in`.tumcampusapp.databinding.FragmentLecturesBinding
import de.tum.`in`.tumcampusapp.di.injector
import javax.inject.Inject

class LecturesFragment : FragmentForSearching<List<AbstractLecture>>(
    R.layout.fragment_lectures,
    R.string.my_lectures,
    authority = LectureSearchSuggestionProvider.AUTHORITY,
    minLength = 4
) {
    @Inject
    lateinit var apiClient: LecturesAPI

    private val binding by viewBinding(FragmentLecturesBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.lecturesComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lecturesListView.setOnItemClickListener { _, _, position, _ ->
            val item = binding.lecturesListView.getItemAtPosition(position) as AbstractLecture
            val intent = Intent(requireContext(), LectureDetailsActivity::class.java)
            intent.putExtra(AbstractLecture.Lecture_ID, item.id)
            startActivity(intent)
        }

        onStartSearch()
    }

    override fun onRefresh() {
        loadPersonalLectures(CacheControl.BYPASS_CACHE)
    }

    override fun onStartSearch() {
        enableRefresh()
        loadPersonalLectures(CacheControl.USE_CACHE)
    }

    override fun onStartSearch(query: String?) {
        query?.let {
            disableRefresh()
            searchLectures(query)
        }
    }

    private fun loadPersonalLectures(cacheControl: CacheControl) {
        fetch { apiClient.getPersonalLectures() }
    }

    private fun searchLectures(query: String) {
        fetch { apiClient.searchLectures(query) }
    }

    override fun onDownloadSuccessful(response: List<AbstractLecture>) {
        if (response.isEmpty()) {
            binding.lecturesListView.adapter = NoResultsAdapter(requireContext())
        } else {
            val lectures = response.sorted()
            binding.lecturesListView.adapter = LecturesListAdapter(requireContext(), lectures.toMutableList())
        }
    }

    companion object {
        @JvmStatic fun newInstance() = LecturesFragment()
    }
}
