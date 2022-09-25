package de.uos.campusapp.component.ui.lectures.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import de.uos.campusapp.R
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.component.other.generic.activity.ActivityForAccessingApi
import de.uos.campusapp.component.ui.lectures.api.LecturesAPI
import de.uos.campusapp.component.ui.lectures.model.AbstractLecture
import de.uos.campusapp.databinding.ActivityLecturedetailsBinding
import de.uos.campusapp.utils.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

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
class LectureDetailsActivity : ActivityForAccessingApi<AbstractLecture>(R.layout.activity_lecturedetails, Component.LECTURES) {

    private lateinit var currentItem: AbstractLecture
    private lateinit var mLectureId: String

    private val compositeDisposable = CompositeDisposable()

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

            val strLectureLanguage = StringBuilder(currentItem.semester?.title ?: getString(R.string.unknown))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_activity_lecture_detail, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItemOpenFiles = menu?.findItem(R.id.action_open_lecture_files)
        val menuItemOpenRecords = menu?.findItem(R.id.action_open_lecture_recordings)

        // Only menu items if configured
        menuItemOpenFiles?.isVisible = ConfigUtils.getConfig(ConfigConst.LECTURES_SHOW_FILES, false)
        menuItemOpenRecords?.isVisible = ConfigUtils.getConfig(ConfigConst.LECTURES_SHOW_RECORDS, false)

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_lecture_files -> {
                openLectureFilesActivity()
                true
            }
            R.id.action_open_lecture_recordings -> {
                compositeDisposable += Single.fromCallable { (apiClient as LecturesAPI).getLectureRecordingsUrl(mLectureId)!! }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::openLectureRecords) {
                        Utils.log(it)
                        Utils.showToast(this, R.string.recordings_open_failed)
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openLectureFilesActivity() {
        // LectureFiles need the name and id of the facing lecture
        val bundle = Bundle()
        bundle.putString(AbstractLecture.Lecture_ID, currentItem.id)
        bundle.putString(Const.TITLE_EXTRA, currentItem.title)

        val intent = Intent(this, LectureFilesActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun openLectureRecords(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
