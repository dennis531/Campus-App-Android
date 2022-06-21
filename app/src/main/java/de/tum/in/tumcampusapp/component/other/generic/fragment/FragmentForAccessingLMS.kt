package de.tum.`in`.tumcampusapp.component.other.generic.fragment

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.api.tumonline.TUMOnlineClient
import de.tum.`in`.tumcampusapp.utils.ConfigUtils

abstract class FragmentForAccessingLMS<T>(
    @LayoutRes layoutId: Int,
    @StringRes titleResId: Int
) : BaseFragment<T>(layoutId, titleResId) {

    protected val apiClient: LMSClient by lazy {
        ConfigUtils.getLMSClient(requireContext())
    }
}
