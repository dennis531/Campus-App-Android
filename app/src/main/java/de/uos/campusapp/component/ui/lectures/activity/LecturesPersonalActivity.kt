package de.uos.campusapp.component.ui.lectures.activity

import android.os.Bundle

import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.component.ui.lectures.fragment.LecturesFragment
import de.uos.campusapp.utils.Component

/**
 * This activity presents the user's lectures. The results can be filtered by the semester.
 * This activity uses the same models as FindLectures.
 */
class LecturesPersonalActivity : BaseActivity(R.layout.activity_lectures, Component.LECTURES) {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, LecturesFragment.newInstance())
                    .commit()
        }
    }
}
