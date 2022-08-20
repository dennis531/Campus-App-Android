package de.uos.campusapp.component.tumui.person.adapteritems

import android.content.Context
import android.content.Intent
import android.net.Uri
import de.uos.campusapp.R

class PhoneContactItem(phoneNumber: String) : AbstractContactItem(R.string.phone, phoneNumber, R.drawable.ic_outline_phone_24px) {

    override fun getIntent(context: Context): Intent {
        val uri = Uri.parse("tel:$value")
        return Intent(Intent.ACTION_DIAL, uri)
    }
}