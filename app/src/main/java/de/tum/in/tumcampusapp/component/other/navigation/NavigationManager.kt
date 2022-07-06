package de.tum.`in`.tumcampusapp.component.other.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.activity.BaseNavigationActivity
import de.tum.`in`.tumcampusapp.component.other.generic.drawer.NavItem
import de.tum.`in`.tumcampusapp.utils.instantiate

object NavigationManager {

    fun open(context: Context, navItem: NavItem) {
        when (navItem) {
            is NavItem.FragmentDestination -> open(context, navItem)
            is NavItem.ActivityDestination -> open(context, navItem)
        }
    }

    //Use FragmentManager.getFragmentFactory() and FragmentFactory.instantiate(ClassLoader, String)

    private fun open(context: Context, navItem: NavItem.FragmentDestination) {
        val activity = context as? BaseNavigationActivity ?: return
        val fragment = activity.supportFragmentManager.instantiate(navItem.fragment.name)
        openFragment(activity, fragment)
    }

    private fun open(context: Context, navItem: NavItem.ActivityDestination) {
        val activity = context as? BaseNavigationActivity ?: return
        val intent = Intent(activity, navItem.activity)
        activity.startActivity(intent)
    }

    fun open(context: Context, destination: NavDestination) {
        when (destination) {
            is NavDestination.Fragment -> {
                val baseNavigationActivity = context as? BaseNavigationActivity ?: return
                val fragment = baseNavigationActivity.supportFragmentManager.instantiate(destination.clazz.name)
                fragment.arguments = destination.args
                openFragment(baseNavigationActivity, fragment)
            }
            is NavDestination.Activity -> {
                val intent = Intent(context, destination.clazz)
                intent.putExtras(destination.args)
                context.startActivity(intent)
            }
            is NavDestination.Link -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(destination.url))
                context.startActivity(intent)
            }
        }
    }

    private fun openFragment(activity: BaseNavigationActivity, fragment: Fragment) {
        activity
            .supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
            .replace(R.id.contentFrame, fragment)
//            .ensureBackToHome(activity)
            .addToBackStack(null)
            .commit()
    }

    private fun FragmentTransaction.ensureBackToHome(
        activity: BaseNavigationActivity
    ): FragmentTransaction {
        if (activity.supportFragmentManager.backStackEntryCount == 0) {
            addToBackStack(null)
        }
        return this
    }
}
