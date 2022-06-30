package de.tum.`in`.tumcampusapp.component.tumui.grades.model

import android.content.Context
import androidx.core.content.ContextCompat
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter
import de.tum.`in`.tumcampusapp.utils.tryOrNull
import org.joda.time.DateTime
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

abstract class AbstractExam : Comparable<AbstractExam>, SimpleStickyListHeadersAdapter.SimpleStickyListItem {
    abstract val id: String?
    abstract val course: String
    abstract val semester: String
    abstract val date: DateTime?
    abstract val examiner: String?
    abstract val grade: Double? // e.g. 1.6 or 2.3
    abstract val type: String?
    abstract val program: String? // Studiengang

    override fun getHeadName() = semester

    override fun getHeaderId() = semester

    override fun compareTo(other: AbstractExam): Int {
        return compareByDescending<AbstractExam> { it.semester }
            .thenByDescending { it.date }
            .thenBy { it.course }
            .compare(this, other)
    }

    open val gradeString: String?
        get() = tryOrNull { GRADE_FORMAT.format(grade) }

    open val isPassed: Boolean
        get() {
            val value = grade ?: 5.0
            return value <= 4.0
        }

    open fun getGradeColor(context: Context): Int {
        // While using getOrDefault() compiles, it results in a NoSuchMethodError on devices with
        // API levels lower than 24.
        // grade colors are assigned to grades like 1,52 as if they were a 1,5
        var resId = R.color.grade_default
        if (grade != null) {
            val cleanGrade = (grade!! * 10.0).toInt() / 10.0
            resId = GRADE_COLORS[cleanGrade] ?: R.color.grade_default
        }
        return ContextCompat.getColor(context, resId)
    }

    companion object {
        private val GRADE_FORMAT = DecimalFormat("0.0").apply { roundingMode = RoundingMode.HALF_UP }

        private val GRADE_COLORS = mapOf(
            1.0 to R.color.grade_1_0,
            1.1 to R.color.grade_1_1,
            1.2 to R.color.grade_1_2,
            1.3 to R.color.grade_1_3,
            1.4 to R.color.grade_1_4,
            1.5 to R.color.grade_1_5,
            1.6 to R.color.grade_1_6,
            1.7 to R.color.grade_1_7,
            1.8 to R.color.grade_1_8,
            1.9 to R.color.grade_1_9,
            2.0 to R.color.grade_2_0,
            2.1 to R.color.grade_2_1,
            2.2 to R.color.grade_2_2,
            2.3 to R.color.grade_2_3,
            2.4 to R.color.grade_2_4,
            2.5 to R.color.grade_2_5,
            2.6 to R.color.grade_2_6,
            2.7 to R.color.grade_2_7,
            2.8 to R.color.grade_2_8,
            2.9 to R.color.grade_2_9,
            3.0 to R.color.grade_3_0,
            3.1 to R.color.grade_3_1,
            3.2 to R.color.grade_3_2,
            3.3 to R.color.grade_3_3,
            3.4 to R.color.grade_3_4,
            3.5 to R.color.grade_3_5,
            3.6 to R.color.grade_3_6,
            3.7 to R.color.grade_3_7,
            3.8 to R.color.grade_3_8,
            3.9 to R.color.grade_3_9,
            4.0 to R.color.grade_4_0,
            4.1 to R.color.grade_4_1,
            4.2 to R.color.grade_4_2,
            4.3 to R.color.grade_4_3,
            4.4 to R.color.grade_4_4,
            4.5 to R.color.grade_4_5,
            4.6 to R.color.grade_4_6,
            4.7 to R.color.grade_4_7,
            4.8 to R.color.grade_4_8,
            4.9 to R.color.grade_4_9,
            5.0 to R.color.grade_5_0
        )
    }
}
