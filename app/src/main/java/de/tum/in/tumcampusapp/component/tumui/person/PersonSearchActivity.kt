package de.tum.`in`.tumcampusapp.component.tumui.person

import android.os.Bundle
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.activity.BaseActivity
import de.tum.`in`.tumcampusapp.utils.Component

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
