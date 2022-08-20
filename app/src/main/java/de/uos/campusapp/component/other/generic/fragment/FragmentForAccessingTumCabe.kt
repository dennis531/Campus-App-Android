package de.uos.campusapp.component.other.generic.fragment

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import de.uos.campusapp.api.general.TUMCabeClient

abstract class FragmentForAccessingTumCabe<T>(
    @LayoutRes layoutId: Int,
    @StringRes titleResId: Int
) : BaseFragment<T>(layoutId, titleResId) {

    protected val apiClient: TUMCabeClient by lazy {
        TUMCabeClient.getInstance(requireContext())
    }
}
