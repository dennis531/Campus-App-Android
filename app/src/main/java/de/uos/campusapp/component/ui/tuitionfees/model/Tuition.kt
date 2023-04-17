package de.uos.campusapp.component.ui.tuitionfees.model

import org.joda.time.DateTime

/**
 * Simple implementation of [AbstractTuition] holding tuition information.
 */
class Tuition(
    override val start: DateTime,
    override val deadline: DateTime,
    override val semester: String,
    override val amount: Double
) : AbstractTuition()
