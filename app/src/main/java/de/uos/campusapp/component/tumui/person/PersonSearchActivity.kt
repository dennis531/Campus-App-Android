package de.uos.campusapp.component.tumui.person

import android.os.Bundle
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.utils.Component

class PersonSearchActivity : BaseActivity(R.layout.activity_person_search, Component.PERSON) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, PersonSearchFragment.newInstance())
                .commit()
        }
    }
}
