package de.uos.campusapp.component.ui.lectures.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.component.other.generic.adapter.NoResultsAdapter
import de.uos.campusapp.component.other.generic.fragment.FragmentForSearching
import de.uos.campusapp.component.ui.lectures.LectureSearchSuggestionProvider
import de.uos.campusapp.component.ui.lectures.activity.LectureDetailsActivity
import de.uos.campusapp.component.ui.lectures.adapter.LecturesListAdapter
import de.uos.campusapp.component.ui.lectures.api.LecturesAPI
import de.uos.campusapp.component.ui.lectures.model.AbstractLecture
import de.uos.campusapp.databinding.FragmentLecturesBinding
import de.uos.campusapp.di.injector
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
