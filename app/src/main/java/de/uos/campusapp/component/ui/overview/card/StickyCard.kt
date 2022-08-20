package de.uos.campusapp.component.ui.overview.card

import android.content.Context
import android.content.SharedPreferences
import de.uos.campusapp.utils.Component

abstract class StickyCard(cardType: Int, context: Context, component: Component? = null) : Card(cardType, context, component) {

    override val isDismissible: Boolean
        get() = false

    override fun discard(editor: SharedPreferences.Editor) {
        // Sticky cards can't be dismissed
    }
}
