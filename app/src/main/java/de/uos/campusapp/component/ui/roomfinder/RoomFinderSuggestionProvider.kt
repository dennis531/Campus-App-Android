package de.uos.campusapp.component.ui.roomfinder

import android.content.SearchRecentSuggestionsProvider

/**
 * Suggestion provider for [RoomFinderActivity]
 */
class RoomFinderSuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, 1)
    }

    companion object {
        const val AUTHORITY = "de.uos.campusapp.component.ui.roomfinder.RoomFinderSuggestionProvider"
    }
}