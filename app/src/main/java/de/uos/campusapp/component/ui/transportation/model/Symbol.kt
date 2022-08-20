package de.uos.campusapp.component.ui.transportation.model

import android.content.Context

/**
 * Base interface for a symbol. A symbol encapsulates information about the symbol shown next to departure information.
 * Contains getter for the background color and text color.
 *
 * @param name Abbreviation of the line (e.g. "RE 18")
 */
interface Symbol {
    val name: String

    /**
     * Gets the RGB value of the symbol background (e.g. 0x3B7233)
     */
    fun getBackgroundColor(context: Context): Int

    /**
     * Gets the RGB value of the symbol text
     */
    fun getTextColor(context: Context): Int

    /**
     * Gets the background RGB value of the highlighted symbol
     */
    fun getHighlightColor(context: Context): Int
}