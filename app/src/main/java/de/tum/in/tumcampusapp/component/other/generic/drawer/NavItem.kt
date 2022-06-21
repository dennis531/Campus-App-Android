package de.tum.`in`.tumcampusapp.component.other.generic.drawer

import androidx.fragment.app.Fragment
import de.tum.`in`.tumcampusapp.component.other.generic.activity.BaseActivity
import de.tum.`in`.tumcampusapp.utils.Component

sealed class NavItem(
    val titleRes: Int,
    val iconRes: Int,
    val component: Component?,
    val needsLMSAccess: Boolean,
    val needsChatAccess: Boolean,
    val hideForEmployees: Boolean
) {

    class FragmentDestination(
        titleRes: Int,
        iconRes: Int,
        val fragment: Class<out Fragment>,
        component: Component? = null,
        needsLMSAccess: Boolean = false,
        needsChatAccess: Boolean = false,
        hideForEmployees: Boolean = false
    ) : NavItem(titleRes, iconRes, component, needsLMSAccess, needsChatAccess, hideForEmployees)

    class ActivityDestination(
        titleRes: Int,
        iconRes: Int,
        val activity: Class<out BaseActivity>,
        component: Component? = null,
        needsLMSAccess: Boolean = false,
        needsChatAccess: Boolean = false,
        hideForEmployees: Boolean = false
    ) : NavItem(titleRes, iconRes, component, needsLMSAccess, needsChatAccess, hideForEmployees)
}
