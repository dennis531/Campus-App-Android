package de.tum.`in`.tumcampusapp.component.tumui.lectures.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter
import de.tum.`in`.tumcampusapp.component.tumui.lectures.activity.LecturesPersonalActivity
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.AbstractLecture
import de.tum.`in`.tumcampusapp.component.ui.chat.activity.ChatRoomsActivity
import java.lang.StringBuilder

/**
 * This class handles the view output of the results for finding lectures via
 * TUMOnline used in [LecturesPersonalActivity]
 * and [ChatRoomsActivity].
 * It implements [se.emilsjolander.stickylistheaders.StickyListHeadersAdapter] to
 * show semester info as sticky header.
 */

class LecturesListAdapter(context: Context, results: MutableList<AbstractLecture>) : SimpleStickyListHeadersAdapter<AbstractLecture>(context, results) {

//    override fun generateHeaderName(item: AbstractLecture) = super.generateHeaderName(item)
//            .replace("Sommersemester", this.context
//                    .getString(R.string.semester_summer))
//            .replace("Wintersemester", this.context
//                    .getString(R.string.semester_winter))

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = inflater.inflate(R.layout.activity_lectures_listview, parent, false)
            holder = ViewHolder()
            holder.tvLectureName = view.findViewById(R.id.lectureNameTextView)
            holder.tvTypeSWSSemester = view.findViewById(R.id.typeTextView)
            holder.tvDozent = view.findViewById(R.id.professorTextView)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val lecture = itemList[position]

        // if we have something to display - set for each lecture element
        holder.tvLectureName?.text = lecture.title

        val details = StringBuilder()
        details.addDetail(lecture.lectureType)
        details.addDetail(lecture.semester)

        if (!lecture.duration.isNullOrBlank()) {
            details.addDetail("${lecture.duration} SWS")
        }

        holder.tvTypeSWSSemester?.text = details

        if (!lecture.lecturers.isNullOrEmpty()) {
            holder.tvDozent?.visibility = View.VISIBLE
            holder.tvDozent?.text = lecture.lecturers!!.first()
        }

        return view
    }

    private fun StringBuilder.addDetail(detail: String?) {
        if (!detail.isNullOrBlank()) {
            if (isNotBlank()) {
                append(" - ")
            }
            append(detail)
        }
    }

    // the layout of the list
    internal class ViewHolder {
        var tvDozent: TextView? = null
        var tvLectureName: TextView? = null
        var tvTypeSWSSemester: TextView? = null
    }
}
