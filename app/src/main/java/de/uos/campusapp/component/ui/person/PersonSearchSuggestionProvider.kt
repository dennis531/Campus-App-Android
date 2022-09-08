package de.uos.campusapp.component.ui.person

import android.content.SearchRecentSuggestionsProvider

/**
 * Suggestion provider for [PersonSearchActivity]
 */
class PersonSearchSuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, 1)
    }

    companion object {
        const val AUTHORITY = "de.uos.campusapp.component.ui.person.PersonSearchSuggestionProvider"
    }
}