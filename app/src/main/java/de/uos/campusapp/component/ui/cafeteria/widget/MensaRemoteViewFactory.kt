package de.uos.campusapp.component.ui.cafeteria.widget

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.cafeteria.controller.CafeteriaManager
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import java.util.*
import java.util.regex.Pattern

class MensaRemoteViewFactory(private val applicationContext: Context) : RemoteViewsService.RemoteViewsFactory {
    private var menus: List<CafeteriaMenuItem> = ArrayList()

    override fun onCreate() {
        menus = CafeteriaManager(applicationContext).bestMatchCafeteriaMenus
    }

    override fun onDataSetChanged() { /* Noop */ }

    override fun onDestroy() { /* Noop */ }

    override fun getCount() = menus.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= menus.size) {
            // No idea why this happens, but getViewAt is occasionally called with position == size
            return loadingView
        }

        val menu = menus[position]
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.mensa_widget_item)

        val menuContent = PATTERN.matcher(menu.name)
                .replaceAll("")
                .trim()
        val menuText = applicationContext.getString(
                R.string.menu_with_long_type_format_string, menuContent, menu.type)
        remoteViews.setTextViewText(R.id.menu_content, menuText)

        val price = menu.getPriceText(applicationContext)
        if (price.isNotEmpty()) {
            remoteViews.setTextViewText(R.id.menu_price, price)
        } else {
            remoteViews.setViewVisibility(R.id.menu_price, View.GONE)
        }

        return remoteViews
    }

    override fun getLoadingView() = RemoteViews(applicationContext.packageName, R.layout.mensa_widget_loading_item)

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = position.toLong()

    override fun hasStableIds() = true

    companion object {

        private val PATTERN = Pattern.compile("\\([^\\)]+\\)")
    }
}
