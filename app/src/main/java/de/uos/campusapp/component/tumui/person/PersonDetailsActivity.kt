package de.uos.campusapp.component.tumui.person

import android.os.Bundle
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.component.tumui.person.adapteritems.*
import de.uos.campusapp.utils.Component

/**
 * Activity to show information about a person at TUM.
 */
class PersonDetailsActivity : BaseActivity(R.layout.activity_person_details, Component.PERSON) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, PersonDetailsFragment.newInstance())
                .commit()
        }
    }
}
