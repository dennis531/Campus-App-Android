package de.tum.`in`.tumcampusapp.component.ui.news

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl.USE_CACHE
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.EqualSpacingItemDecoration
import de.tum.`in`.tumcampusapp.component.other.generic.fragment.FragmentForDownloadingExternal
import de.tum.`in`.tumcampusapp.databinding.FragmentNewsBinding
import de.tum.`in`.tumcampusapp.di.injector
import de.tum.`in`.tumcampusapp.service.DownloadWorker
import de.tum.`in`.tumcampusapp.utils.NetUtils
import de.tum.`in`.tumcampusapp.utils.Utils
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
