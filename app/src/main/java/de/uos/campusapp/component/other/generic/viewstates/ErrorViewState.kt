package de.uos.campusapp.component.other.generic.viewstates

import de.uos.campusapp.R

/**
 * Represents the view state in case of an error. This class is mostly used in [ProgressActivity]
 * when a request to the API fails.
 *
 * @param iconResId The resource ID of any icon that should be displayed, optional
 * @param headerResId The resource ID of any header text that should be displayed, optional
 * @param messageResId The resource ID of any error message that should be displayed
 * @param buttonTextResId The resource ID of the text that should be displayed on the button, defaults to "Retry"
 */
sealed class ErrorViewState(
    val iconResId: Int? = null,
    val headerResId: Int? = null,
    val messageResId: Int,
    val buttonTextResId: Int = R.string.retry
)

class EmptyViewState(iconResId: Int? = null, messageResId: Int) : ErrorViewState(
        iconResId = iconResId,
        messageResId = messageResId
)

class NoInternetViewState : ErrorViewState(
        iconResId = R.drawable.ic_no_wifi,
        messageResId = R.string.no_internet_connection
)

class FailedApiViewState(messageResId: Int) : ErrorViewState(
        headerResId = R.string.error_accessing_api_header,
        messageResId = messageResId
)

class UnknownErrorViewState(messageResId: Int) : ErrorViewState(messageResId = messageResId)
