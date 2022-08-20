package de.uos.campusapp.component.ui.transportation.model

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import de.uos.campusapp.R

/**
 * Encapsulates information about the symbol shown next to departure information. Contains the
 * background color and text color, as computed based on the line type.
 *
 * @param name Line symbol name e.g. U6, S1, T14
 */
class SimpleSymbol : Symbol {

    override val name: String

    @ColorRes
    private val backgroundColorResId: Int

    @ColorRes
    private val textColorResId: Int

    /**
     * Creates a symbol with specified background and text colors given as color resources
     */
    constructor(name: String, @ColorRes backgroundColorResId: Int, @ColorRes textColorResId: Int) {
        this.name = name
        this.backgroundColorResId = backgroundColorResId
        this.textColorResId = textColorResId
    }

    /**
     * Generates colors based on common name prefixes
     */
    constructor(name: String) {
        this.name = name

        var textColor = R.color.white
        val backgroundColor: Int

        val digitIndex = name.indexOfFirst { it.isDigit() || it.isWhitespace() }

        val symbol = if (digitIndex != -1) name.substring(0, digitIndex) else ""

        // val numberStart = if (digitIndex != -1) digitIndex else 0
        // val lineNumber =  name.substring(numberStart).trim().toIntOrNull() ?: 0

        when (symbol) {
            "S" -> {
                backgroundColor = R.color.tram
            }
            "U" -> {
                backgroundColor = R.color.subway
            }
            "RE" -> {
                backgroundColor = R.color.regional_express
            }
            "RB" -> {
                backgroundColor = R.color.regional_bahn
            }
            "IRE" -> {
                backgroundColor = R.color.interregio_express
            }
            "N" -> {
                backgroundColor = R.color.black
                textColor = R.color.night_line_font
            }
            "X" -> backgroundColor = R.color.express_bus
            else -> backgroundColor = R.color.regular_bus
        }

        this.textColorResId = textColor
        this.backgroundColorResId = backgroundColor
    }

    override fun getBackgroundColor(context: Context): Int {
        return ContextCompat.getColor(context, backgroundColorResId)
    }

    override fun getTextColor(context: Context): Int {
        return ContextCompat.getColor(context, textColorResId)
    }

    override fun getHighlightColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.reduced_opacity) and ContextCompat.getColor(context, backgroundColorResId)
    }
}
