package de.uos.campusapp.component.ui.grades

import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.utils.Component

class GradesActivity : BaseActivity(R.layout.activity_grades, Component.GRADES) {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, GradesFragment.newInstance())
                .commit()
        }
    }

    companion object {

        // TODO Eventually use Intent to BaseActivity with Intent extra
        fun newIntent(context: Context) = Intent(context, GradesActivity::class.java)
    }
}
