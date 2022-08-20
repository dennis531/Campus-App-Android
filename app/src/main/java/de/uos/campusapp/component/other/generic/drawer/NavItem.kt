package de.uos.campusapp.component.other.generic.drawer

import androidx.fragment.app.Fragment
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.utils.Component

sealed class NavItem(
    val titleRes: Int,
    val iconRes: Int,
    val component: Component?,
    val needsChatAccess: Boolean,
    val hideForEmployees: Boolean
) {

    class FragmentDestination(
        titleRes: Int,
        iconRes: Int,
        val fragment: Class<out Fragment>,
        component: Component? = null,
        needsChatAccess: Boolean = false,
        hideForEmployees: Boolean = false
    ) : NavItem(titleRes, iconRes, component, needsChatAccess, hideForEmployees)

    class ActivityDestination(
        titleRes: Int,
        iconRes: Int,
        val activity: Class<out BaseActivity>,
        component: Component? = null,
        needsChatAccess: Boolean = false,
        hideForEmployees: Boolean = false
    ) : NavItem(titleRes, iconRes, component, needsChatAccess, hideForEmployees)
}
