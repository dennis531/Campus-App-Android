package de.tum.`in`.tumcampusapp.component.tumui.lectures.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.component.other.generic.activity.ActivityForAccessingApi
import de.tum.`in`.tumcampusapp.component.tumui.lectures.adapter.LectureAppointmentsListAdapter
import de.tum.`in`.tumcampusapp.component.tumui.lectures.api.LecturesAPI
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.AbstractLecture
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.LectureAppointmentInterface
import de.tum.`in`.tumcampusapp.databinding.ActivityLecturesappointmentsBinding
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.Const

/**
 * This activity provides the appointment dates to a given lecture using the
 * TUMOnline web service.
 *
 *
 * HINT: a valid TUM Online token is needed
 *
 *
 * NEEDS: stp_sp_nr and title set in incoming bundle (lecture id, title)
 */
class LecturesAppointmentsActivity : ActivityForAccessingApi<List<LectureAppointmentInterface>>(R.layout.activity_lecturesappointments, Component.LECTURES) {

    private var lectureId: String? = null

    private lateinit var binding: ActivityLecturesappointmentsBinding


    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        binding = ActivityLecturesappointmentsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tvTermineLectureName.text = intent.getStringExtra(Const.TITLE_EXTRA)
        lectureId = intent.getStringExtra(AbstractLecture.Lecture_ID)

        if (lectureId == null) {
            finish()
            return
        }

        loadLectureAppointments(lectureId, CacheControl.USE_CACHE)
    }

    override fun onRefresh() {
        loadLectureAppointments(lectureId, CacheControl.BYPASS_CACHE)
    }

    private fun loadLectureAppointments(lectureId: String?, cacheControl: CacheControl) {
        lectureId?.let {
            fetch { (apiClient as LecturesAPI).getLectureAppointments(lectureId) }
        }
    }

    override fun onDownloadSuccessful(response: List<LectureAppointmentInterface>) {
        if (response.isNullOrEmpty()) {
            showError(R.string.no_appointments)
            return
        }

        binding.lvTerminList.adapter = LectureAppointmentsListAdapter(this, response)
    }
}
