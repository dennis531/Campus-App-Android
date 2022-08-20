package de.uos.campusapp.component.tumui.lectures

import android.content.SearchRecentSuggestionsProvider

import de.uos.campusapp.component.tumui.lectures.activity.LecturesPersonalActivity

/**
 * Suggestion provider for [LecturesPersonalActivity]
 */
class LectureSearchSuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, 1)
    }

    companion object {
        const val AUTHORITY = "de.uos.campusapp.component.tumui.lectures.LectureSearchSuggestionProvider"
    }
}