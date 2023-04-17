package de.uos.campusapp.component.ui.person.model

import java.io.Serializable

/**
 * Represents an institute
 *
 * @property name Institute name
 */
interface InstituteInterface : Serializable {
    var name: String

    companion object {
        private const val serialVersionUID = 2906210652697246506L
    }
}
