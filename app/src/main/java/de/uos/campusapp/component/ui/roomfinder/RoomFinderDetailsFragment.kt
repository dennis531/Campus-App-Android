package de.uos.campusapp.component.ui.roomfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.roomfinder.model.RoomFinderRoomInterface
import de.uos.campusapp.databinding.FragmentRoomfinderDetailsBinding

class RoomFinderDetailsFragment : Fragment() {

    private val room: RoomFinderRoomInterface by lazy {
        arguments?.getSerializable(ROOM) as RoomFinderRoomInterface
    }

    private var binding: FragmentRoomfinderDetailsBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRoomfinderDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding!!) {
            campusTextView.text = room.campus
            campusTextView.isVisible = !room.campus.isNullOrBlank()

            addressTextView.text = room.address
            addressTextView.isVisible = !room.address.isNullOrBlank()

            // Hide layout if no address or campus is available
            addressLinearLayout.isVisible = !(room.campus.isNullOrBlank() && room.address.isNullOrBlank())

            infoTextView.text = if (!room.info.isNullOrBlank()) room.info else getString(R.string.no_info_available)

            // Load image
            if (!room.imageUrl.isNullOrBlank()) {
                loadRoomImage()
            } else {
                roomImageView.visibility = View.GONE
            }
        }
    }

    private fun loadRoomImage() {
        Picasso.get()
            .load(room.imageUrl)
            .into(binding?.roomImageView, object : Callback {
                override fun onSuccess() {
                    // Left empty on purpose
                }

                override fun onError(e: Exception) {
                    binding?.roomImageView?.visibility = View.GONE
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val ROOM = "room"

        @JvmStatic
        fun newInstance(room: RoomFinderRoomInterface) =
            RoomFinderDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ROOM, room)
                }
            }
    }
}