package de.uos.campusapp.component.tumui.lectures.adapter

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import de.uos.campusapp.R
import de.uos.campusapp.component.tumui.lectures.model.FileInterface
import org.joda.time.format.DateTimeFormat
import java.lang.StringBuilder

class LectureFilesAdapter(
    private val items: List<FileInterface>,
    private val onItemClick: (FileInterface) -> Unit
) : RecyclerView.Adapter<LectureFilesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lecture_file_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(file: FileInterface, onItemClick: (FileInterface) -> Unit) = with(itemView) {
            val nameTextView = findViewById<TextView>(R.id.nameTextView)
            val dateTextView = findViewById<TextView>(R.id.dateTextView)
            val detailTextView = findViewById<TextView>(R.id.detailTextView)

            nameTextView.text = file.name

            if (file.date != null) {
                val date = DATE_FORMAT.print(file.date)
                dateTextView.text = String.format("%s: %s", context.getString(R.string.date), date)
            } else {
                dateTextView.visibility = View.GONE
            }

            // Create detail string
            val detailString = StringBuilder()
            file.author?.let {
                detailString.append(context.getString(R.string.author_format_string, it))
            }

            file.size?.let {
                if (detailString.isNotEmpty()) {
                    detailString.append(", ")
                }

                // Format file size
                val formattedSize = Formatter.formatShortFileSize(context, it)
                detailString.append(context.getString(R.string.size_format_string, formattedSize))
            }

            detailTextView.text = detailString
            detailTextView.isVisible = detailString.isNotEmpty()

            setOnClickListener { onItemClick(file) }
        }
    }

    companion object {
        private val DATE_FORMAT = DateTimeFormat.shortDateTime()
    }
}
