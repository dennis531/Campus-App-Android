package de.tum.`in`.tumcampusapp.component.tumui.tuitionfees.model

import org.joda.time.DateTime

/**
 * Concrete class holding tuition information.
 */
class Tuition(
    override val start: DateTime,
    override val deadline: DateTime,
    override val semester: String,
    override val amount: Double
): AbstractTuition()
