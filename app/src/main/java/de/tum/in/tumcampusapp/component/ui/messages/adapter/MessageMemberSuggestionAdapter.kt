package de.tum.`in`.tumcampusapp.component.ui.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import de.tum.`in`.tumcampusapp.component.ui.messages.model.MessageMember

class MessageMemberSuggestionAdapter(
    private val context: Context,
    private var members: List<MessageMember>
): BaseAdapter(), Filterable {

    override fun getCount(): Int {
        return members.size
    }

    override fun getItem(position: Int): MessageMember {
        return members[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = members[position].toString()

        return view
    }

    fun updateSuggestions(members: List<MessageMember>) {
        this.members = members
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                // We don't filter the members as the members should have been filtered by the api
                return FilterResults().apply {
                    values = members
                    count = members.size
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // We don't change the data
            }
        }
    }

}