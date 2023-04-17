package de.uos.campusapp.component.ui.messages.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.work.WorkManager
import com.google.android.material.chip.Chip
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.fragment.BaseFragment
import de.uos.campusapp.component.ui.messages.MessagesController
import de.uos.campusapp.component.ui.messages.adapter.MessageMemberSuggestionAdapter
import de.uos.campusapp.component.ui.messages.model.*
import de.uos.campusapp.databinding.FragmentCreateMessageBinding
import de.uos.campusapp.di.injector
import de.uos.campusapp.service.SendMessageWorker
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import de.uos.campusapp.utils.plusAssign
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import javax.inject.Inject

class CreateMessageFragment : BaseFragment<Unit>(
    R.layout.fragment_create_message,
    R.string.new_message
) {

    @Inject
    lateinit var manager: MessagesController

    private var replyMessage: AbstractMessage? = null

    private val recipients: MutableList<AbstractMessageMember> = mutableListOf()
    private val recipientSuggestionAdapter by lazy {
        MessageMemberSuggestionAdapter(requireContext(), emptyList())
    }

    // for delayed suggestions
    private val delayHandler: Handler = Handler(Looper.getMainLooper())
    private val suggestionRunnable: Runnable = Runnable { getRecipientSuggestions() }

    private val compositeDisposable = CompositeDisposable()

    private val binding by viewBinding(FragmentCreateMessageBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.messagesComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val intent = requireActivity().intent
        if (intent.hasExtra(Const.MESSAGE_REPLY)) {
            replyMessage = intent.getParcelableExtra(Const.MESSAGE_REPLY)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recipients view
        setupRecipientsTextView()

        // Setup reply message
        replyMessage?.let {
            // Remove reply prefix
            val subject = it.subject.replace(REPLY_PATTERN, "")

            binding.subjectEditText.setText(getString(R.string.reply_format_string, subject))

            val sender = it.sender
            if (sender != null) {
                addRecipient(sender)
            }
        }
    }

    private fun setupRecipientsTextView() {
        with(binding) {
            recipientsAutoCompleteTextView.threshold = THRESHOLD
            recipientsAutoCompleteTextView.setAdapter(recipientSuggestionAdapter)

            recipientsAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                val recipient = recipientSuggestionAdapter.getItem(position)

                addRecipient(recipient)
            }

            recipientsAutoCompleteTextView.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.log("Search")
                    delayHandler.removeCallbacks(suggestionRunnable)
                    getRecipientSuggestions()

                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(recipientsAutoCompleteTextView.getWindowToken(), 0)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            recipientsAutoCompleteTextView.doOnTextChanged { text, start, before, count ->
                delayHandler.removeCallbacks(suggestionRunnable)
                binding.recipientsAutoCompleteTextView.error = null

                if (text == null || text.length < THRESHOLD) {
                    return@doOnTextChanged
                }

                var containsDigit = false
                for (i in 0 until text.length) {
                    if (Character.isDigit(text.get(i))) {
                        containsDigit = true
                        break
                    }
                }
                if (containsDigit) {
                    // don't try to get new suggestions (we don't autocomplete IDs)
                    return@doOnTextChanged
                }

                delayHandler.postDelayed(suggestionRunnable, DELAY)
            }
        }
    }

    private fun getRecipientSuggestions() {
        val query = binding.recipientsAutoCompleteTextView.text.toString()

        compositeDisposable += Single.fromCallable { manager.searchRecipients(query) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.isEmpty()) {
                    binding.recipientsAutoCompleteTextView.error = getString(R.string.error_user_not_found)
                }

                recipientSuggestionAdapter.updateSuggestions(response)
            }, {
                binding.recipientsAutoCompleteTextView.error = getString(R.string.error_user_not_found)
            })
    }

    private fun addRecipient(recipient: AbstractMessageMember) {
        with(binding) {
            if (recipients.contains(recipient)) {
                recipientsAutoCompleteTextView.error = getString(R.string.error_recipient_selected)
                return
            }

            recipients.add(recipient)

            // Create chip displaying recipient's name
            val chip: Chip = layoutInflater.inflate(R.layout.chip, recipientsChipGroup, false) as Chip

            chip.apply {
                tag = recipient
                text = recipient.name

                setChipIconResource(R.drawable.ic_person_outline_24px)
                setChipIconTintResource(R.color.white)

                setOnCloseIconClickListener {
                    val r = it.tag
                    recipients.remove(r)

                    recipientsChipGroup.removeView(it)
                }
            }

            recipientsChipGroup.addView(chip)

            // Scroll to new added chip
            recipientsScrollView.post {
                recipientsScrollView.smoothScrollTo(chip.right, 0)
            }

            // Clear text
            recipientsAutoCompleteTextView.setText("")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_create_message, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_send -> {
                sendMessage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendMessage() {
        val message = getMessage()

        // Check message validity
        if (!checkValidity(message)) {
            return
        }

        // Add outbox message
        manager.addMessage(message)

        // Activate send message worker
        WorkManager.getInstance()
            .enqueue(SendMessageWorker.getWorkRequest())

        Utils.showToast(requireContext(), R.string.message_sending)
        requireActivity().finish()
    }

    /**
     * Creates a message from the user inputs
     */
    private fun getMessage(): AbstractMessage {
        with(binding) {
            val sender = MessageMember(
                Utils.getSetting(requireContext(), Const.PROFILE_ID, ""),
                Utils.getSetting(requireContext(), Const.PROFILE_DISPLAY_NAME, "")
            )

            return Message(
                "",
                subjectEditText.text.toString(),
                messageEditText.text.toString(),
                MessageType.OUTBOX,
                sender,
                recipients,
                DateTime.now()
            )
        }
    }

    /**
     * Checks if the message has all needed fields
     */
    private fun checkValidity(message: AbstractMessage): Boolean {
        if (message.recipients.isEmpty()) {
            Utils.showToast(requireContext(), R.string.error_recipient_missing)
            return false
        }

        if (message.subject.isBlank()) {
            Utils.showToast(requireContext(), R.string.error_subject_missing)
            return false
        }

        if (message.text.isBlank()) {
            Utils.showToast(requireContext(), R.string.error_message_missing)
            return false
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        private const val THRESHOLD = 3 // min number of characters before getting suggestions
        private const val DELAY = 1000L // millis after user stopped typing before getting suggestions

        private val REPLY_PATTERN = Regex("^Re:\\s*", RegexOption.IGNORE_CASE)

        fun newInstance() = CreateMessageFragment()
    }
}