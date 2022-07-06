package de.tum.`in`.tumcampusapp.component.other.generic.activity

import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.api.tumonline.TUMOnlineClient
import de.tum.`in`.tumcampusapp.utils.ConfigUtils

/**
 * This Activity can be extended by concrete Activities that access information from TUMonline. It
 * includes methods for fetching content (both via [TUMOnlineClient] and from the local
 * cache, and implements error and retry handling.
 *
 * @param T The type of object that is loaded from the TUMonline API
 */
@Deprecated("Use BaseActivity and a suitable BaseFragment class")
abstract class ActivityForAccessingLMS<T>(layoutId: Int) : ProgressActivity<T>(layoutId) {

    protected val apiClient: LMSClient by lazy {
        ConfigUtils.getLMSClient(this)
    }
}
