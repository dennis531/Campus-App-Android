package de.uos.campusapp.component.other.generic.activity

import de.uos.campusapp.api.general.TUMCabeClient
import de.uos.campusapp.api.tumonline.TUMOnlineClient

/**
 * This Activity can be extended by concrete Activities that access information from TUM Cabe. It
 * includes methods for fetching content (both via [TUMOnlineClient] and from the local
 * cache, and implements error and retry handling.
 *
 * @param T The type of object that is loaded from the TUMCabe API
 */
@Deprecated("Use BaseActivity and a suitable BaseFragment class")
abstract class ActivityForAccessingTumCabe<T>(layoutId: Int) : ProgressActivity<T>(layoutId) {

    protected val apiClient: TUMCabeClient by lazy {
        TUMCabeClient.getInstance(this)
    }
}