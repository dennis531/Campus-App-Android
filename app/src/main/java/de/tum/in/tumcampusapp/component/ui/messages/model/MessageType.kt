package de.tum.`in`.tumcampusapp.component.ui.messages.model

import androidx.annotation.StringRes
import de.tum.`in`.tumcampusapp.R

enum class MessageType(val id: Int, @StringRes val titleResId: Int) {
    INBOX(0, R.string.inbox),
    SENT(1, R.string.sent),
    OUTBOX(2, R.string.outbox);

    companion object {
        private val map = values().associateBy(MessageType::id)

        fun fromId(id: Int) = map[id] ?: INBOX
    }
}
