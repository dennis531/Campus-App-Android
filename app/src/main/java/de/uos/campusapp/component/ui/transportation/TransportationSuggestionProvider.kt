package de.uos.campusapp.component.ui.transportation

import android.content.SearchRecentSuggestionsProvider

class TransportationSuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, 1)
    }

    companion object {
        const val AUTHORITY = "de.uos.campusapp.component.ui.transportation.TransportationSuggestionProvider" // TODO: Rename
    }
}