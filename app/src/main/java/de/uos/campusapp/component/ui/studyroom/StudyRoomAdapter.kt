package de.uos.campusapp.component.ui.studyroom

import android.app.SearchManager
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.roomfinder.RoomFinderActivity
import de.uos.campusapp.component.ui.studyroom.model.StudyRoomItem
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Utils
import org.joda.time.format.DateTimeFormat
import java.util.*

class StudyRoomAdapter(private val fragment: Fragment, private val studyRooms: List<StudyRoomItem>) :
        RecyclerView.Adapter<StudyRoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyRoomViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_studyroom_detail, parent, false)
        return StudyRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudyRoomViewHolder, position: Int) {
        val room = studyRooms[position]

        holder.apply {
            openRoomFinderButton.setText(R.string.go_to_room)
            openRoomFinderButton.tag = room.name
            openRoomFinderButton.isVisible = ConfigUtils.isComponentEnabled(itemView.context, Component.ROOMFINDER)

            headerTextView.text = room.name
            val isOccupied = room.occupiedUntil != null && !room.occupiedUntil!!.isBeforeNow

            val detailsText = StringBuilder()
            if (!room.info.isNullOrBlank()) {
                detailsText.append("${room.info}")
            }
            if (isOccupied) {
                val time = DateTimeFormat.forPattern("HH:mm")
                        .withLocale(Locale.getDefault())
                        .print(room.occupiedUntil)
                detailsText.append("<br>${fragment.getString(R.string.occupied)} <b>$time</b>")
            }

            detailsTextView.text = Utils.fromHtml(detailsText.toString())

            val colorResId = if (isOccupied) R.color.study_room_occupied else R.color.study_room_free
            val color = ContextCompat.getColor(holder.itemView.context, colorResId)
            cardView.setCardBackgroundColor(color)

            /* Overwrite click listener from xml and open roomfinder */
            openRoomFinderButton.setOnClickListener {
                val roomName = it.tag as String

                with(Intent(it.context, RoomFinderActivity::class.java)) {
                    putExtra(SearchManager.QUERY, roomName)
                    it.context.startActivity(this)
                }
            }
        }
    }

    override fun getItemCount() = studyRooms.size
}