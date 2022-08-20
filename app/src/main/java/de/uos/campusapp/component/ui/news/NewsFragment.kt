package de.uos.campusapp.component.ui.news

import android.content.Context
import android.os.Bundle
import android.view.View
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.api.tumonline.CacheControl.USE_CACHE
import de.uos.campusapp.component.other.generic.adapter.EqualSpacingItemDecoration
import de.uos.campusapp.component.other.generic.fragment.FragmentForDownloadingExternal
import de.uos.campusapp.databinding.FragmentNewsBinding
import de.uos.campusapp.di.injector
import de.uos.campusapp.service.DownloadWorker
import de.uos.campusapp.utils.NetUtils
import java.lang.Math.round
import javax.inject.Inject

class NewsFragment : FragmentForDownloadingExternal(
    R.layout.fragment_news,
    R.string.news
) {

    @Inject
    lateinit var newsController: NewsController

    @Inject
    lateinit var newsDownloadAction: DownloadWorker.Action

    override val method: DownloadWorker.Action
        get() = newsDownloadAction

    private var firstVisibleItemPosition: Int? = null

    private val binding by viewBinding(FragmentNewsBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.newsComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        requestDownload(USE_CACHE)
    }

    private fun initRecyclerView() {
        val spacing = round(resources.getDimension(R.dimen.material_card_view_padding))
        binding.newsRecyclerView.addItemDecoration(EqualSpacingItemDecoration(spacing))
    }

    override fun onStart() {
        super.onStart()

        // Gets all news from database
        val news = newsController.getAllFromDb(requireContext())
        if (news.isEmpty()) {
            if (NetUtils.isConnected(requireContext())) {
                showErrorLayout()
            } else {
                showNoInternetLayout()
            }
            return
        }

        with(binding) {
            val adapter = NewsAdapter(requireContext(), news)
            newsRecyclerView.adapter = adapter

            // Restore previous state (including selected item index and scroll position)
            val firstVisiblePosition = firstVisibleItemPosition ?: newsController.todayIndex
            newsRecyclerView.scrollToPosition(firstVisiblePosition)
        }


        showLoadingEnded()
    }

    companion object {
        fun newInstance() = NewsFragment()
    }
}
