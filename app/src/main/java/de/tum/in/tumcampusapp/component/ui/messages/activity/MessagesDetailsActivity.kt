package de.tum.`in`.tumcampusapp.component.ui.messages.activity

import android.os.Bundle
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.activity.BaseActivity
import de.tum.`in`.tumcampusapp.component.ui.messages.fragment.MessagesDetailsFragment
import de.tum.`in`.tumcampusapp.component.ui.messages.model.AbstractMessage
import de.tum.`in`.tumcampusapp.utils.Component

class MessagesDetailsActivity : BaseActivity(R.layout.activity_messages_details, Component.MESSAGES) {

    private val message: AbstractMessage by lazy {
        intent.getParcelableExtra(EXTRA_MESSAGE)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, MessagesDetailsFragment.newInstance(message))
                .commit()
        }
    }

    companion object {
        const val EXTRA_MESSAGE = "message"
    }
}