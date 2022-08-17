package de.tum.`in`.tumcampusapp.component.other.generic.fragment

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import de.tum.`in`.tumcampusapp.utils.Component
import de.tum.`in`.tumcampusapp.utils.ConfigUtils

abstract class FragmentForAccessingApi<T>(
    @LayoutRes layoutId: Int,
    @StringRes titleResId: Int,
    component: Component
) : BaseFragment<T>(layoutId, titleResId) {

    protected val apiClient: de.tum.`in`.tumcampusapp.api.generic.BaseAPI by lazy {
        ConfigUtils.getApiClient(requireContext(), component)
    }
}
