package de.uos.campusapp.component.ui.messages.adapter

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.messages.model.AbstractMessage
import de.uos.campusapp.component.ui.messages.model.MessageType
import org.joda.time.DateTime

/**
 * [RecyclerView.Adapter] that can display a [AbstractMessage].
 */
class MessagesAdapter(
    private var messages: List<AbstractMessage>,
    private val isBigLayout: Boolean,
    private val onItemClick: (AbstractMessage) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private var lastDate: DateTime? = null

    private val itemLayout: Int by lazy {
        if (isBigLayout) R.layout.message_item_big else R.layout.message_item_small
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(itemLayout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position], lastDate, onItemClick)
    }

    override fun getItemCount(): Int = messages.size

    fun update(messages: List<AbstractMessage>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    /**
     * Messages after the last date will be highlighted
     */
    fun setLastDate(date: DateTime) {
        lastDate = date
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val subjectTextView = itemView.findViewById<TextView>(R.id.subjectTextView)
        private val memberTextView = itemView.findViewById<TextView>(R.id.memberTextView)
        private val dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)

        fun bind(message: AbstractMessage, lastDate: DateTime?, onItemClick: (AbstractMessage) -> Unit) = with(itemView) {
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

            // highlight messages after last date
            lastDate?.let {
                if (message.date.isAfter(lastDate)) {
                    highlight()
                }
            }

            setOnClickListener { onItemClick(message) }
        }

        private fun highlight() {
            subjectTextView.setTypeface(subjectTextView.typeface, Typeface.BOLD)
        }
    }

}