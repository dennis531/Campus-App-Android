package de.tum.`in`.tumcampusapp.component.ui.messages.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.ui.messages.model.AbstractMessage
import de.tum.`in`.tumcampusapp.component.ui.messages.model.MessageType

/**
 * [RecyclerView.Adapter] that can display a [AbstractMessage].
 */
class MessagesAdapter(
    private var messages: List<AbstractMessage>,
    private val isBigLayout: Boolean,
    private val onItemClick: (AbstractMessage) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val itemLayout: Int by lazy {
        if (isBigLayout) R.layout.message_item_big else R.layout.message_item_small
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(itemLayout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position], onItemClick)
    }

    override fun getItemCount(): Int = messages.size

    fun update(messages: List<AbstractMessage>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(message: AbstractMessage, onItemClick: (AbstractMessage) -> Unit) = with(itemView) {
            val subjectTextView = findViewById<TextView>(R.id.subjectTextView)
            val memberTextView = findViewById<TextView>(R.id.memberTextView)
            val dateTextView = findViewById<TextView>(R.id.dateTextView)

            subjectTextView.text = message.subject
            dateTextView.text = message.formattedDate

            if (message.type == MessageType.INBOX) {
                memberTextView.text = message.sender?.name
                memberTextView.isVisible = message.sender != null
            } else {
                if (message.recipients.size > 1) {
                    memberTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_outline_people_24px, 0, 0, 0)
                }
                memberTextView.text = message.recipients.joinToString("\n")
                memberTextView.isVisible = message.recipients.isNotEmpty()
            }

            setOnClickListener { onItemClick(message) }
        }
    }

}