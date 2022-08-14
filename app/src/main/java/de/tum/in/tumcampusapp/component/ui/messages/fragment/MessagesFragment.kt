package de.tum.`in`.tumcampusapp.component.ui.messages.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.component.other.generic.fragment.FragmentForDownloadingExternal
import de.tum.`in`.tumcampusapp.component.ui.messages.MessagesController
import de.tum.`in`.tumcampusapp.component.ui.messages.activity.CreateMessageActivity
import de.tum.`in`.tumcampusapp.component.ui.messages.adapter.MessagesAdapter
import de.tum.`in`.tumcampusapp.component.ui.messages.model.AbstractMessage
import de.tum.`in`.tumcampusapp.component.ui.messages.model.MessageType
import de.tum.`in`.tumcampusapp.databinding.FragmentMessagesBinding
import de.tum.`in`.tumcampusapp.di.injector
import de.tum.`in`.tumcampusapp.service.DownloadWorker
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
import kotlinx.android.synthetic.main.fragment_messages.*
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * A fragment representing a list of messages.
 */
class MessagesFragment : FragmentForDownloadingExternal(
    R.layout.fragment_messages,
    R.string.messages
) {

    @Inject
    lateinit var manager: MessagesController

    @Inject
    lateinit var messagesDownloadAction: DownloadWorker.Action

    override val method: DownloadWorker.Action
        get() = messagesDownloadAction

    private var selectedMessageType: MessageType = MessageType.INBOX

    private var messages: List<AbstractMessage> = emptyList()

    private val binding by viewBinding(FragmentMessagesBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.messagesComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()

        binding.messagesRecyclerView.setHasFixedSize(true)
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.messagesRecyclerView.addItemDecoration(itemDecoration)

        binding.addMessageActionButton.setOnClickListener {
            val intent = Intent(requireContext(), CreateMessageActivity::class.java)
            startActivity(intent)
        }

        requestDownload(CacheControl.USE_CACHE)
    }

    private fun setupTabs() {
        with(binding) {
            messagesTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    selectedMessageType = MessageType.valueOf(tab.tag as String)
                    loadMessages()
                }

                override fun onTabUnselected(tab: TabLayout.Tab) = Unit

                override fun onTabReselected(tab: TabLayout.Tab) {
                    messagesRecyclerView.smoothScrollToPosition(0)
                }

            })
        }

        // Add for each message type
        MessageType.values().forEach {
            messagesTabs.addTab(messagesTabs.newTab().setText(it.titleResId).setTag(it.name))
        }
    }

    override fun onStart() {
        super.onStart()

        loadMessages()
    }

    private fun loadMessages() {
        messages = manager.getAllMessagesByType(selectedMessageType)

        with(binding) {
            val lastDateMillis = Utils.getSettingLong(requireContext(), Const.MESSAGE_LAST_DATE, 0)
            val lastDate = DateTime(lastDateMillis)

            val adapter = MessagesAdapter(messages, true, this@MessagesFragment::onItemClick)
            adapter.setLastDate(lastDate)
            messagesRecyclerView.adapter = adapter
        }

        showLoadingEnded()
    }

    /**
     * Handles message click events
     */
    private fun onItemClick(message: AbstractMessage) {
        openMessageDetails(message)
    }

    private fun openMessageDetails(message: AbstractMessage) {
        val intent = message.getIntent(requireContext())
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()

        // Save date of last seen message
        messages.firstOrNull()?.let {
            Utils.setSetting(requireContext(), Const.MESSAGE_LAST_DATE, it.date.millis)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = MessagesFragment()
    }
}
