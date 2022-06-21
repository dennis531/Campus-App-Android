package de.tum.`in`.tumcampusapp.component.tumui.person

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.tumui.person.model.Person
import de.tum.`in`.tumcampusapp.component.tumui.person.model.PersonInterface

class PersonSearchResultsAdapter(
    private var items: List<PersonInterface>,
    private val onItemClick: (PersonInterface) -> Unit
) : RecyclerView.Adapter<PersonSearchResultsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.person_search_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount() = items.size

    fun update(items: List<PersonInterface>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            person: PersonInterface,
            onItemClick: (PersonInterface) -> Unit
        ) = with(itemView) {
            val textView = findViewById<TextView>(R.id.textView)
            textView.text = person.fullName
            setOnClickListener { onItemClick(person) }
        }
    }
}