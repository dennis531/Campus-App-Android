package de.uos.campusapp.component.other.generic.activity

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.api.tumonline.TUMOnlineClient
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils

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
