package de.uos.campusapp.component.ui.news

import android.os.Bundle
import de.uos.campusapp.R
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.utils.Component

class NewsActivity : BaseActivity(R.layout.activity_news, Component.NEWS) {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, NewsFragment.newInstance())
                .commit()
        }
    }
}
