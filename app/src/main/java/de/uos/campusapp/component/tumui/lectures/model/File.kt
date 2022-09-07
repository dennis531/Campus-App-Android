package de.uos.campusapp.component.tumui.lectures.model

import org.joda.time.DateTime

/**
 * Simple implementation of [FileInterface]
 */
class File(
    override val id: String,
    override val name: String,
    override val mimeType: String,
    override val date: DateTime?,
    override val author: String?,
    override val size: Long?
) : FileInterface
