package de.uos.campusapp.component.tumui.person.adapteritems

import android.content.Context
import android.content.Intent
import android.net.Uri
import de.uos.campusapp.R

class EmailContactItem(emailAddress: String) : AbstractContactItem(R.string.e_mail, emailAddress, R.drawable.ic_outline_email_24px) {

    override fun getIntent(context: Context) = Intent(Intent.ACTION_SENDTO).apply {
        type = "text/plain"
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(value))
    }
}