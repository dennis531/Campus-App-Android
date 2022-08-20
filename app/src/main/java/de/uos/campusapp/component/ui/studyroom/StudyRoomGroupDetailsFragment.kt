package de.uos.campusapp.component.ui.studyroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.adapter.GridEqualSpacingDecoration
import de.uos.campusapp.utils.Const

/**
 * Fragment for each study room group. Shows study room details in a list.
 */
class StudyRoomGroupDetailsFragment : Fragment() {
    private var groupId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            if (containsKey(Const.STUDY_ROOM_GROUP_ID)) {
                groupId = getString(Const.STUDY_ROOM_GROUP_ID, "")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_study_room_group_details, container, false)
        val studyRooms = StudyRoomGroupManager(requireContext()).getAllStudyRoomsForGroup(groupId)

        if (studyRooms.isEmpty()) {
            rootView.findViewById<View>(R.id.study_room_placeholder).visibility = View.VISIBLE
            return rootView
        }

        rootView.findViewById<RecyclerView>(R.id.fragment_item_detail_recyclerview).apply {
            val spanCount = 2
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = StudyRoomAdapter(this@StudyRoomGroupDetailsFragment, studyRooms)

            val spacing = Math.round(resources.getDimension(R.dimen.material_card_view_padding))
            addItemDecoration(GridEqualSpacingDecoration(spacing, spanCount))
        }
        return rootView
    }
}
