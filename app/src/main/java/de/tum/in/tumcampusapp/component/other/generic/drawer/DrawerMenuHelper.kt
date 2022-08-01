package de.tum.`in`.tumcampusapp.component.other.generic.drawer

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.settings.SettingsActivity
import de.tum.`in`.tumcampusapp.component.tumui.calendar.CalendarFragment
import de.tum.`in`.tumcampusapp.component.tumui.grades.GradesFragment
import de.tum.`in`.tumcampusapp.component.tumui.lectures.fragment.LecturesFragment
import de.tum.`in`.tumcampusapp.component.tumui.person.PersonSearchFragment
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.RoomFinderFragment
import de.tum.`in`.tumcampusapp.component.tumui.tuitionfees.TuitionFeesFragment
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.fragment.CafeteriaFragment
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatRoomsFragment
import de.tum.`in`.tumcampusapp.component.ui.news.NewsFragment
import de.tum.`in`.tumcampusapp.component.ui.openinghours.OpeningHoursListFragment
import de.tum.`in`.tumcampusapp.component.ui.overview.InformationActivity
import de.tum.`in`.tumcampusapp.component.ui.overview.MainFragment
import de.tum.`in`.tumcampusapp.component.ui.studyroom.StudyRoomsFragment
import de.tum.`in`.tumcampusapp.component.ui.transportation.TransportationActivity
import de.tum.`in`.tumcampusapp.utils.*

class DrawerMenuHelper(
    private val activity: AppCompatActivity,
    private val navigationView: NavigationView
) {

    private val currentFragment: Fragment?
        get() = activity.supportFragmentManager.findFragmentById(R.id.contentFrame)

    private val navigationMenu: Menu
        get() = navigationView.menu

    private val allItems = mutableListOf<NavItem>()

    fun populateMenu() {
        val hasLMSAccess = ConfigUtils.getAuthManager(activity).hasAccess()
        val isChatEnabled = Utils.getSettingBool(activity, Const.GROUP_CHAT_ENABLED, false)
        val isEmployeeMode = Utils.getSettingBool(activity, Const.EMPLOYEE_MODE, false)

        navigationMenu.clear()
        allItems.clear()

        navigationMenu += HOME
        allItems += HOME

        val myCampusMenu = navigationMenu.addSubMenu(R.string.my_campus)
        val myCampusCandidates = MY_CAMPUS
            .filterNot { !isComponentEnabled(it.component) }
            .filterNot { it.needsLMSAccess && !hasLMSAccess }
            .filterNot { !isChatEnabled && it.needsChatAccess }
            .filterNot { isEmployeeMode && it.hideForEmployees }

        for (candidate in myCampusCandidates) {
            myCampusMenu += candidate
            allItems += candidate
        }

        val generalMenu = navigationMenu.addSubMenu(R.string.common_info)
        val generalCandidates = GENERAL
            .filterNot { !isComponentEnabled(it.component) }
            .filterNot { it.needsLMSAccess && !hasLMSAccess }
        for (candidate in generalCandidates) {
            generalMenu += candidate
            allItems += candidate
        }

        val aboutMenu = navigationMenu.addSubMenu(R.string.about)
        for (item in ABOUT) {
            aboutMenu += item
            allItems += item
        }

        highlightCurrentItem()
    }

    private fun isComponentEnabled(component: Component?): Boolean {
        return component?.let { ConfigUtils.isComponentEnabled(activity, component) } ?: true
    }

    fun findNavItem(menuItem: MenuItem): NavItem {
        if (menuItem.title == activity.getString(HOME.titleRes)) {
            return HOME
        }

        for (item in MY_CAMPUS + GENERAL) {
            if (menuItem.title == activity.getString(item.titleRes)) {
                return item
            }
        }

        for (item in ABOUT) {
            if (menuItem.title == activity.getString(item.titleRes)) {
                return item
            }
        }

        throw IllegalArgumentException("Invalid menu item ${menuItem.title} provided")
    }

    fun updateNavDrawer() {
        highlightCurrentItem()
    }

    private fun highlightCurrentItem() {
        val items = navigationMenu.allItems
        items.forEach { it.isChecked = false }

        val currentIndex = allItems
            .mapNotNull { it as? NavItem.FragmentDestination }
            .indexOfFirst { it.fragment == currentFragment?.javaClass }

        if (currentIndex != -1) {
            items[currentIndex].isCheckable = true
            items[currentIndex].isChecked = true
        }
    }

    private companion object {

        private val HOME = NavItem.FragmentDestination(R.string.home, R.drawable.ic_outline_home_24px, MainFragment::class.java, Component.OVERVIEW)

        private val MY_CAMPUS: Array<NavItem> = arrayOf(
                NavItem.FragmentDestination(R.string.calendar, R.drawable.ic_outline_event_24px, CalendarFragment::class.java, Component.CALENDAR, true),
                NavItem.FragmentDestination(R.string.my_lectures, R.drawable.ic_outline_school_24px, LecturesFragment::class.java, Component.LECTURES,true, hideForEmployees = true),
                NavItem.FragmentDestination(R.string.chat_rooms, R.drawable.ic_outline_chat_bubble_outline_24px, ChatRoomsFragment::class.java, Component.CHAT, true, true),
                NavItem.FragmentDestination(R.string.my_grades, R.drawable.ic_outline_insert_chart_outlined_24px, GradesFragment::class.java, Component.GRADES, true, hideForEmployees = true),
                NavItem.FragmentDestination(R.string.tuition_fees, R.drawable.ic_money, TuitionFeesFragment::class.java, Component.TUITIONFEES, hideForEmployees = true)
        )

        private val GENERAL: Array<NavItem> = arrayOf(
                NavItem.FragmentDestination(R.string.menues, R.drawable.ic_cutlery, CafeteriaFragment::class.java, Component.CAFETERIA),
                NavItem.FragmentDestination(R.string.study_rooms, R.drawable.ic_outline_group_work_24px, StudyRoomsFragment::class.java, Component.STUDYROOM),
                NavItem.FragmentDestination(R.string.roomfinder, R.drawable.ic_outline_location_on_24px, RoomFinderFragment::class.java, Component.ROOMFINDER, true),
                NavItem.FragmentDestination(R.string.person_search, R.drawable.ic_outline_people_outline_24px, PersonSearchFragment::class.java, Component.PERSON, true),
                NavItem.FragmentDestination(R.string.news, R.drawable.ic_rss, NewsFragment::class.java, Component.NEWS, true),
                // NavItem.FragmentDestination(R.string.barrier_free, R.drawable.ic_outline_accessible_24px, BarrierFreeInfoFragment::class.java),
                NavItem.FragmentDestination(R.string.opening_hours, R.drawable.ic_outline_access_time_24px, OpeningHoursListFragment::class.java, Component.OPENINGHOUR),
                NavItem.ActivityDestination(R.string.transport, R.drawable.ic_outline_train_24px, TransportationActivity::class.java, Component.TRANSPORTATION)
        )

        private val ABOUT: Array<NavItem> = arrayOf(
                // NavItem.ActivityDestination(R.string.show_feedback, R.drawable.ic_outline_feedback_24px, FeedbackActivity::class.java),
                NavItem.ActivityDestination(R.string.about_tca, R.drawable.ic_action_info, InformationActivity::class.java),
                NavItem.ActivityDestination(R.string.settings, R.drawable.ic_outline_settings_24px, SettingsActivity::class.java)
        )
    }
}
