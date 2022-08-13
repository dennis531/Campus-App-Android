package de.tum.`in`.tumcampusapp.component.ui.messages.activity

import android.os.Bundle
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.activity.BaseActivity
import de.tum.`in`.tumcampusapp.component.ui.messages.fragment.CreateMessageFragment
import de.tum.`in`.tumcampusapp.utils.Component

class CreateMessageActivity: BaseActivity(R.layout.activity_create_message, Component.MESSAGES) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, CreateMessageFragment.newInstance())
                .commit()
        }
    }
}