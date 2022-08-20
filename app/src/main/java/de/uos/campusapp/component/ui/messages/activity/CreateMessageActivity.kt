package de.uos.campusapp.component.ui.messages.activity

import android.os.Bundle
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.component.ui.messages.fragment.CreateMessageFragment
import de.uos.campusapp.utils.Component

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