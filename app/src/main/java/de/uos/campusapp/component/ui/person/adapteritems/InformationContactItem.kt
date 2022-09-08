package de.uos.campusapp.component.ui.person.adapteritems

import android.content.Context
import android.content.Intent
import de.uos.campusapp.R

class InformationContactItem(text: String) : AbstractContactItem(R.string.additional_info, text, R.drawable.ic_action_info) {

    override fun getIntent(context: Context): Intent? = null
}