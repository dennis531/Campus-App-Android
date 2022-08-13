package de.tum.`in`.tumcampusapp.component.ui.messages.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.activity.BaseActivity
import de.tum.`in`.tumcampusapp.component.ui.messages.MessagesController
import de.tum.`in`.tumcampusapp.component.ui.messages.activity.CreateMessageActivity
import de.tum.`in`.tumcampusapp.component.ui.messages.model.AbstractMessage
import de.tum.`in`.tumcampusapp.databinding.FragmentMessagesDetailsBinding
import de.tum.`in`.tumcampusapp.di.injector
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
import de.tum.`in`.tumcampusapp.utils.plusAssign
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MessagesDetailsFragment : Fragment() {

    @Inject
    lateinit var manager: MessagesController

    private val message: AbstractMessage by lazy {
        arguments?.getParcelable(MESSAGE)!!
    }

    val baseActivity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    private val compositeDisposable = CompositeDisposable()

    private val binding by viewBinding(FragmentMessagesDetailsBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.messagesComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_messages_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up toolbar
        val toolbar = baseActivity.findViewById<Toolbar>(R.id.toolbar)
        baseActivity.setSupportActionBar(toolbar)

        baseActivity.supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        // Show message
        with(binding) {
            subjectTextView.text = message.subject

            senderTextView.text = getString(R.string.sender_format_string, message.sender?.name)
            senderTextView.isVisible = message.sender != null

            val recipientNames = message.recipients.joinToString(", ") { it.name }
            recipientsTextView.text = getString(R.string.recipients_format_string, recipientNames)
            recipientsTextView.isVisible = message.recipients.isNotEmpty()

            dateTextView.text = message.formattedDate

            messageTextView.text = Utils.fromHtml(message.text)

            replyButton.isVisible = message.replyable
            replyButton.setOnClickListener { replyMessage() }
        }
    }

    private fun replyMessage() {
        val intent = Intent(requireContext(), CreateMessageActivity::class.java)
        intent.putExtra(Const.MESSAGE_REPLY, message)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_messages_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                showDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_message)
            .setMessage(resources.getString(R.string.delete_message_body))
            .setPositiveButton(R.string.delete) { _, _ -> deleteMessage() }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
        dialog.show()
    }

    private fun deleteMessage() {
        compositeDisposable += Completable.fromCallable { manager.deleteMessage(message) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Utils.showToast(requireContext(), R.string.done)
                baseActivity.finish()
            }, {
                Utils.log(it, "Failure deleting message")
                Utils.showToast(requireContext(), R.string.error_something_wrong)
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        private const val MESSAGE = "message"

        @JvmStatic
        fun newInstance(message: AbstractMessage) =
            MessagesDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(MESSAGE, message)
                }
            }
    }

}