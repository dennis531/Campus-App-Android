package de.uos.campusapp.component.tumui.person

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.uos.campusapp.R
import de.uos.campusapp.component.tumui.person.model.InstituteInterface

class PersonGroupsAdapter(private val items: List<InstituteInterface>) : RecyclerView.Adapter<PersonGroupsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.person_group_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(institute: InstituteInterface) = with(itemView) {
            val iconImageView = findViewById<ImageView>(R.id.iconImageView)
            val labelTextView = findViewById<TextView>(R.id.labelTextView)
            val orgTextView = findViewById<TextView>(R.id.orgTextView)

            iconImageView.visibility = if (adapterPosition == 0) View.VISIBLE else View.INVISIBLE
            labelTextView.text = context.getString(R.string.institute)
            orgTextView.text = institute.name
        }
    }
}