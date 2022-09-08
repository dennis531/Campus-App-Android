package de.uos.campusapp.component.ui.lectures.model

import org.joda.time.DateTime

/**
 * Holds information to a lecture file
 *
 * @property id identifier
 * @property name Name of file
 * @property mimeType Media type, e.g. "application/pdf"
 * @property date Change or create date (optional)
 * @property author Name of file owner or/and creator (optional)
 * @property size Size in byte (optional)
 */
interface FileInterface {
    val id: String
    val name: String
    val mimeType: String
    val date: DateTime?
    val author: String?
    val size: Long?
}
