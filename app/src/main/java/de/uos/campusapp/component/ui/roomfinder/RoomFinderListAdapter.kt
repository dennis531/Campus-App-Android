package de.uos.campusapp.component.ui.roomfinder

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter
import de.uos.campusapp.component.ui.roomfinder.model.RoomFinderRoomInterface

/**
 * Custom UI adapter for a list of employees.
 */
class RoomFinderListAdapter(
    context: Context,
    items: List<RoomFinderRoomInterface>
) : SimpleStickyListHeadersAdapter<RoomFinderRoomInterface>(context, items.toMutableList()) {

    internal class ViewHolder {
        var tvRoomTitle: TextView? = null
        var tvBuildingTitle: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_roomfinder_item, parent, false)
            holder = ViewHolder()
            holder.tvRoomTitle = view.findViewById(R.id.startup_actionbar_title)
            holder.tvBuildingTitle = view.findViewById(R.id.building)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val room = itemList[position]
        // Setting all values in listView
        holder.tvRoomTitle?.text = room.info ?: context.getString(R.string.no_info_available)
        holder.tvBuildingTitle?.text = room.address ?: context.getString(R.string.no_address_available)
        return view
    }
}
