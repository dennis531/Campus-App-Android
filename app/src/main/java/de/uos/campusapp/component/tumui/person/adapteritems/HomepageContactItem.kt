package de.uos.campusapp.component.tumui.person.adapteritems

import android.content.Context
import android.content.Intent
import android.net.Uri
import de.uos.campusapp.R

class HomepageContactItem(url: String) : AbstractContactItem(R.string.homepage, url, R.drawable.ic_outline_public_24px) {

    override fun getIntent(context: Context) = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(value)
    }
}