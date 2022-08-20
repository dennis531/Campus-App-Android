package de.uos.campusapp.component.ui.studyroom

import com.google.android.material.button.MaterialButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.uos.campusapp.R

class StudyRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var cardView: CardView = itemView.findViewById(R.id.card_view)
    var headerTextView: TextView = itemView.findViewById(R.id.headerTextView)
    var detailsTextView: TextView = itemView.findViewById(R.id.detailsTextView)
    var openRoomFinderButton: MaterialButton = itemView.findViewById(R.id.openLinkButton)
}