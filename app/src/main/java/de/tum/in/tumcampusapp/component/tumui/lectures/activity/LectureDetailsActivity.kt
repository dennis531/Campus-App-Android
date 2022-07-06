package de.tum.`in`.tumcampusapp.component.tumui.lectures.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.tumonline.CacheControl
import de.tum.`in`.tumcampusapp.component.other.generic.activity.ActivityForAccessingLMS
import de.tum.`in`.tumcampusapp.component.tumui.lectures.api.LecturesAPI
import de.tum.`in`.tumcampusapp.component.tumui.lectures.model.AbstractLecture
import de.tum.`in`.tumcampusapp.databinding.ActivityLecturedetailsBinding
import de.tum.`in`.tumcampusapp.utils.Const

/**
 * This Activity will show all details found on the TUMOnline web service
 * identified by its lecture id (which has to be posted to this activity by
 * bundle).
 *
 *
 * There is also the opportunity to get all appointments which are related to
 * this lecture by clicking the button on top of the view.
 *
 *
 * HINT: a valid TUM Online token is needed
 *
 *
 * NEEDS: stp_sp_nr set in incoming bundle (lecture id)
 */
class LectureDetailsActivity : ActivityForAccessingLMS<AbstractLecture>(R.layout.activity_lecturedetails) {

    private lateinit var currentItem: AbstractLecture
    private lateinit var mLectureId: String

    private lateinit var binding: ActivityLecturedetailsBinding

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        binding = ActivityLecturedetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appointmentsButton.setOnClickListener {
            // LectureAppointments need the name and id of the facing lecture
            val bundle = Bundle()
            bundle.putString(AbstractLecture.Lecture_ID, currentItem.id)
            bundle.putString(Const.TITLE_EXTRA, currentItem.title)

            val intent = Intent(this, LecturesAppointmentsActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        mLectureId = intent.getStringExtra(AbstractLecture.Lecture_ID)!!
        loadLectureDetails(mLectureId, CacheControl.USE_CACHE)
    }

    override fun onRefresh() {
        loadLectureDetails(mLectureId, CacheControl.BYPASS_CACHE)
    }

    private fun loadLectureDetails(lectureId: String, cacheControl: CacheControl) {
        fetch { (apiClient as LecturesAPI).getLectureDetails(lectureId) }
    }

    override fun onDownloadSuccessful(response: AbstractLecture) {
        currentItem = response

        with(binding) {
            lectureNameTextView.text = currentItem.title

            val strLectureLanguage = StringBuilder(currentItem.semester ?: getString(R.string.unknown))
            if (!currentItem.mainLanguage.isNullOrBlank()) {
                strLectureLanguage.append(" - ").append(currentItem.mainLanguage)
            }
            semesterTextView.text = strLectureLanguage

            val strSws = StringBuilder(currentItem.lectureType ?: getString(R.string.unknown))
            if (!currentItem.duration.isNullOrBlank()) {
                strLectureLanguage.append(" - ").append("${currentItem.duration} SWS")
            }
            swsTextView.text = strSws

            val lecturers = currentItem.lecturers
            if (lecturers.isNullOrEmpty()) {
                professorTextView.isVisible = false
            } else {
                professorTextView.text = lecturers.joinToString(", ")
            }

            val institute = currentItem.institute
            if (institute.isNullOrBlank()) {
                orgTextView.isVisible = false
            } else {
                orgTextView.text = institute
            }

            if (institute.isNullOrBlank() && lecturers.isNullOrEmpty()) {
                contributorsHeaderTextView.isVisible = false
            }

            val lectureContent = currentItem.lectureContent
            if (lectureContent.isNullOrBlank()) {
                contentHeaderTextView.isVisible = false
                contentTextView.isVisible = false
            } else {
                contentTextView.text = lectureContent
            }

            val teachingMethod = currentItem.teachingMethod
            if (teachingMethod.isNullOrBlank()) {
                methodHeaderTextView.isVisible = false
                methodTextView.isVisible = false
            } else {
                methodTextView.text = teachingMethod
            }

            val targets = currentItem.teachingTargets
            if (targets.isNullOrBlank()) {
                targetsHeaderTextView.isVisible = false
                targetsTextView.isVisible = false
            } else {
                targetsTextView.text = targets
            }

            appointmentsButton.isEnabled = true
        }

    }
}
