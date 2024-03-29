package de.uos.campusapp.component.other.generic.drawer

import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.onboarding.OnboardingActivity
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils

class DrawerHeaderInflater(
    private val context: Context
) {

    fun inflater(navigationView: NavigationView) {
        val headerView = navigationView.inflateHeaderView(R.layout.drawer_header)
        val imageView = headerView.findViewById<CircleImageView>(R.id.profileImageView)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameTextView)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailTextView)
        val loginButton = headerView.findViewById<MaterialButton>(R.id.loginButton)

        val isLoggedIn = ConfigUtils.getAuthManagers(context).all { it.hasAccess() }

        if (isLoggedIn) {
            val name = Utils.getSetting(context, Const.PROFILE_DISPLAY_NAME, "")
            if (name.isNotEmpty()) {
                nameTextView.text = name
            } else {
                nameTextView.visibility = View.INVISIBLE
            }

            val email = Utils.getSetting(context, Const.PROFILE_EMAIL, "")
            if (email.isNotEmpty()) {
                emailTextView.text = email
            } else {
                emailTextView.visibility = View.GONE
            }

            loginButton.visibility = View.GONE
        } else {
            nameTextView.visibility = View.GONE
            emailTextView.visibility = View.GONE
            imageView.visibility = View.GONE

            loginButton.visibility = View.VISIBLE
            loginButton.setOnClickListener {
                val intent = OnboardingActivity.newIntent(context)
                context.startActivity(intent)
            }
        }

        fetchProfilePicture(headerView)

        val divider = headerView.findViewById<View>(R.id.divider)
        val rainbowBar = headerView.findViewById<View>(R.id.rainbow_bar)

        if (Utils.getSettingBool(context, Const.RAINBOW_MODE, false)) {
            divider.visibility = View.GONE
            rainbowBar.visibility = View.VISIBLE
        } else {
            divider.visibility = View.VISIBLE
            rainbowBar.visibility = View.GONE
        }
    }

    private fun fetchProfilePicture(headerView: View) {
        val url = Utils.getSetting(context, Const.PROFILE_PICTURE_URL, "")
        if (url.isEmpty()) {
            return
        }

        val imageView = headerView.findViewById<CircleImageView>(R.id.profileImageView)
        Picasso.get()
            .load(url)
            .error(R.drawable.photo_not_available)
            .placeholder(R.drawable.photo_not_available)
            .into(imageView)
    }
}
