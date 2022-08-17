package de.tum.`in`.tumcampusapp.component.other.generic.activity

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.api.tumonline.TUMOnlineClient
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils

/**
 * This Activity can be extended by concrete Activities that access information from TUMonline. It
 * includes methods for fetching content (both via [TUMOnlineClient] and from the local
 * cache, and implements error and retry handling.
 *
 * @param T The type of object that is loaded from the TUMonline API
 */
@Deprecated("Use BaseActivity and a suitable BaseFragment class")
abstract class ActivityForAccessingApi<T>(layoutId: Int, component: Component) : ProgressActivity<T>(layoutId, component) {

    protected val apiClient: BaseAPI by lazy {
        ConfigUtils.getApiClient(this, component)
    }
}
