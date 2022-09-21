package de.uos.campusapp.component.ui.legacy.feedback.model

data class FeedbackResult(
    var success: String = "",
    var error: String = ""
) {

    val isSuccess: Boolean
        get() = error.isEmpty() && success.isNotEmpty()
}
