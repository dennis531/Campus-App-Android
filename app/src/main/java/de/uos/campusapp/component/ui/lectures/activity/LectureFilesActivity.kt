package de.uos.campusapp.component.ui.lectures.activity

import android.os.Bundle
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.component.ui.lectures.fragment.LectureFilesFragment
import de.uos.campusapp.utils.Component

/**
 * This activity provides the files of a given lecture
 */
class LectureFilesActivity : BaseActivity(R.layout.activity_lecture_files, Component.LECTURES) {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, LectureFilesFragment.newInstance())
                .commit()
        }
    }
}