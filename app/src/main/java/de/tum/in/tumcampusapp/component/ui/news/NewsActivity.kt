package de.tum.`in`.tumcampusapp.component.ui.news

import android.os.Bundle
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.activity.BaseActivity
import de.tum.`in`.tumcampusapp.utils.Component

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
