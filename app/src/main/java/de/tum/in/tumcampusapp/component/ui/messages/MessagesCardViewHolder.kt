package de.tum.`in`.tumcampusapp.component.ui.messages

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.ui.messages.adapter.MessagesAdapter
import de.tum.`in`.tumcampusapp.component.ui.messages.model.AbstractMessage
import de.tum.`in`.tumcampusapp.component.ui.overview.CardInteractionListener
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
import org.joda.time.DateTime
import kotlin.math.min

class MessagesCardViewHolder(
    itemView: View,
    interactionListener: CardInteractionListener
) : CardViewHolder(itemView, interactionListener) {

    private val messagesNameTextView = itemView.findViewById<TextView>(R.id.messagesNameTextView)
    private val messagesRecyclerView = itemView.findViewById<RecyclerView>(R.id.messagesRecyclerView)

    private lateinit var adapter: MessagesAdapter

    fun bind(messages: List<AbstractMessage>) {
        with(itemView) {
            messagesNameTextView.text = context.getString(R.string.messages)

            val itemCount = min(messages.size, 3)
            val latestMessages = messages.take(itemCount)

            if (this@MessagesCardViewHolder::adapter.isInitialized.not()) {
                messagesRecyclerView.layoutManager = LinearLayoutManager(context)
                messagesRecyclerView.itemAnimator = DefaultItemAnimator()

                adapter = MessagesAdapter(latestMessages, false) {
                    val intent = it.getIntent(context)
                    context.startActivity(intent)
                }
                messagesRecyclerView.adapter = adapter
            } else {
                adapter.update(latestMessages)
            }

            // Set date when messages overview was last opened
            val lastDateMillis = Utils.getSettingLong(context, Const.MESSAGE_LAST_DATE, 0)
            val lastDate = DateTime(lastDateMillis)

            adapter.setLastDate(lastDate)
        }
    }
}