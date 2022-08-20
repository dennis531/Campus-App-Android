package de.uos.campusapp.component.other.generic.fragment

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import de.uos.campusapp.utils.Component
import de.uos.campusapp.utils.ConfigUtils

abstract class FragmentForAccessingApi<T>(
    @LayoutRes layoutId: Int,
    @StringRes titleResId: Int,
    component: Component
) : BaseFragment<T>(layoutId, titleResId) {

    protected val apiClient: de.uos.campusapp.api.generic.BaseAPI by lazy {
        ConfigUtils.getApiClient(requireContext(), component)
    }
}
