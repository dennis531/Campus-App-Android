package de.uos.campusapp.component.ui.cafeteria.widget

import android.content.Intent
import android.widget.RemoteViewsService

class MensaWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return MensaRemoteViewFactory(this.applicationContext)
    }
}
