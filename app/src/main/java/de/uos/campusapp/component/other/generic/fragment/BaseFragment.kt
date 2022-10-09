package de.uos.campusapp.component.other.generic.fragment

import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import de.uos.campusapp.R
import de.uos.campusapp.api.general.exception.UnauthorizedException
import de.uos.campusapp.api.general.exception.ForbiddenException
import de.uos.campusapp.api.general.exception.NotFoundException
import de.uos.campusapp.component.other.generic.activity.BaseActivity
import de.uos.campusapp.component.other.generic.viewstates.EmptyViewState
import de.uos.campusapp.component.other.generic.viewstates.ErrorViewState
import de.uos.campusapp.component.other.generic.viewstates.FailedApiViewState
import de.uos.campusapp.component.other.generic.viewstates.NoInternetViewState
import de.uos.campusapp.component.other.generic.viewstates.UnknownErrorViewState
import de.uos.campusapp.utils.*
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.support.v4.runOnUiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException
import java.util.concurrent.Callable

abstract class BaseFragment<T>(
    @LayoutRes private val layoutId: Int,
    @StringRes private val titleResId: Int
) : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val loadingDisposable = CompositeDisposable()

    private var apiCall: Call<T>? = null
    private var hadSuccessfulRequest = false

    private val toolbar: Toolbar?
        get() = requireActivity().findViewById<Toolbar?>(R.id.toolbar)

    private val contentView: ViewGroup
        get() =  requireActivity().findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup

    protected var swipeRefreshLayout: SwipeRefreshLayout? = null

    private val errorLayoutsContainer: FrameLayout by lazy {
        requireActivity().findViewById<FrameLayout>(R.id.errors_layout)
    }

    private val errorLayout: LinearLayout by lazy {
        requireActivity().findViewById<LinearLayout>(R.id.error_layout)
    }

    private val errorIconImageView: ImageView by lazy {
        requireActivity().findViewById<ImageView>(R.id.iconImageView)
    }

    private val errorHeaderTextView: TextView by lazy {
        errorLayout.findViewById<TextView>(R.id.headerTextView)
    }

    private val errorMessageTextView: TextView by lazy {
        errorLayout.findViewById<TextView>(R.id.messageTextView)
    }

    private val errorButton: MaterialButton by lazy {
        errorLayout.findViewById<MaterialButton>(R.id.button)
    }

    private val progressLayout: FrameLayout by lazy {
        requireActivity().findViewById<FrameLayout>(R.id.progress_layout)
    }

    private val baseActivity: BaseActivity
        get() = requireActivity() as BaseActivity

    private var registered: Boolean = false

    private val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            runOnUiThread {
                this@BaseFragment.onRefresh()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        swipeRefreshLayout = requireActivity().findViewById<SwipeRefreshLayout?>(R.id.swipeRefreshLayout)
        // If content is refreshable setup the SwipeRefreshLayout
        swipeRefreshLayout?.apply {
            setOnRefreshListener(this@BaseFragment)
            setColorSchemeResources(
                    R.color.color_primary,
                    R.color.tum_A100,
                    R.color.tum_A200
            )
        }
    }

    override fun onResume() {
        super.onResume()
        toolbar?.setTitle(titleResId)
    }

    private fun setupToolbar() {
        val toolbar = toolbar ?: return
        baseActivity.setSupportActionBar(toolbar)

        baseActivity.supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
    }

    /**
     * Fetches a call and uses the provided listener if the request was successful.
     *
     * @param call The [Call] to fetch
     */
    protected fun fetch(call: Call<T>) {
        apiCall = call

        showLoadingStart()
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                apiCall = null
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    hadSuccessfulRequest = true
                    onDownloadSuccessful(body)
                } else if (response.isSuccessful) {
                    onEmptyDownloadResponse()
                } else {
                    onDownloadUnsuccessful(response.code())
                }
                showLoadingEnded()
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if (call.isCanceled) {
                    return
                }

                apiCall = null
                showLoadingEnded()
                onDownloadFailure(t)
            }
        })
    }

    protected fun fetch(maybe: Maybe<T>) {
        showLoadingStart()
        loadingDisposable += maybe
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                onDownloadSuccessful(response)
                showLoadingEnded()
            }, {
                showLoadingEnded()
                onDownloadFailure(it)
            }, {
                showLoadingEnded()
                onEmptyDownloadResponse()
            })
    }

    protected fun fetch(callable: Callable<T>) {
        fetch(Maybe.fromCallable(callable))
    }

    /**
     * Called if the response from the API call is successful. Provides the unwrapped response body.
     * Subclasses need to override this method to be alerted of successful responses after calling
     * the [fetch] method.
     */
    open fun onDownloadSuccessful(response: T) = Unit

    /**
     * Called if the response from the API call is successful, but empty.
     */
    protected open fun onEmptyDownloadResponse() {
        showError(R.string.error_no_data_to_show)
    }

    /**
     * Called when a response is received, but that response is not successful. Displays the
     * appropriate error message, either in an error layout, or as a dialog or Snackbar.
     */
    protected fun onDownloadUnsuccessful(statusCode: Int) {
        if (statusCode == 503) {
            // The service is unavailable
            showErrorSnackbar(R.string.error_api_unavailable)
        } else {
            showErrorSnackbar(R.string.error_unknown)
        }
    }

    /**
     * Called when an Exception is raised during an API call. Displays an error layout.
     * @param throwable The error that has occurred
     */
    protected fun onDownloadFailure(throwable: Throwable) {
        Utils.log(throwable)

        if (hadSuccessfulRequest) {
            showErrorSnackbar(throwable)
        } else {
            showErrorLayout(throwable)
        }
    }

    /**
     * Shows error layout and toasts the given message.
     * Hides any progress indicator.
     *
     * @param messageResId Resource id of the error text
     */
    protected fun showError(messageResId: Int) {
        runOnUiThread {
            showError(UnknownErrorViewState(messageResId))
        }
    }

    private fun showErrorSnackbar(t: Throwable) {
        val messageResId = when (t) {
            is UnknownHostException -> R.string.no_internet_connection
            is UnauthorizedException -> R.string.error_unauthorized
            is ForbiddenException -> R.string.error_no_rights_to_access_function
            is NotFoundException -> R.string.error_resource_not_found
            else -> R.string.error_unknown
        }

        showErrorSnackbar(messageResId)
    }

    protected fun showErrorSnackbar(messageResId: Int) {
        runOnUiThread {
            Snackbar.make(contentView, messageResId, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) { retryRequest() }
                    .setActionTextColor(Color.WHITE)
                    .show()
        }
    }

    private fun showErrorLayout(throwable: Throwable) {
        when (throwable) {
            is UnknownHostException -> showNoInternetLayout()
            is UnauthorizedException -> showFailedApiLayout(R.string.error_unauthorized)
            is ForbiddenException -> showFailedApiLayout(R.string.error_no_rights_to_access_function)
            is NotFoundException -> showFailedApiLayout(R.string.error_resource_not_found)
            else -> showError(R.string.error_unknown)
        }
    }

    private fun showFailedApiLayout(messageResId: Int = R.string.error_accessing_api_body) {
        runOnUiThread {
            showError(FailedApiViewState(messageResId))
        }
    }

    protected fun showNoInternetLayout() {
        runOnUiThread {
            showError(NoInternetViewState())
        }

        val request = NetworkRequest.Builder()
                .addCapability(NetUtils.internetCapability)
                .build()

        if (registered.not()) {
            baseActivity.connectivityManager.registerNetworkCallback(request, networkCallback)
            registered = true
        }
    }

    protected fun showEmptyResponseLayout(messageResId: Int, iconResId: Int? = null) {
        runOnUiThread {
            showError(EmptyViewState(iconResId, messageResId))
        }
    }

    protected fun showContentLayout() {
        runOnUiThread {
            errorLayout.visibility = View.GONE
        }
    }

    protected fun showErrorLayout() {
        runOnUiThread {
            errorLayout.visibility = View.VISIBLE
        }
    }

    private fun showError(viewState: ErrorViewState) {
        showLoadingEnded()

        errorIconImageView.setImageResourceOrHide(viewState.iconResId)
        errorHeaderTextView.setTextOrHide(viewState.headerResId)
        errorMessageTextView.setTextOrHide(viewState.messageResId)

        errorButton.setTextOrHide(viewState.buttonTextResId)
        errorButton.setOnClickListener { retryRequest() }

        errorLayoutsContainer.visibility = View.VISIBLE
        errorLayout.visibility = View.VISIBLE
    }

    /**
     * Enables [SwipeRefreshLayout]
     */
    protected fun enableRefresh() {
        swipeRefreshLayout?.isEnabled = true
    }

    /**
     * Disables [SwipeRefreshLayout]
     */
    protected fun disableRefresh() {
        swipeRefreshLayout?.isEnabled = false
    }

    /**
     * Gets called when Pull-To-Refresh layout was used to refresh content.
     * Should start the background refresh task.
     * Override this if you use a [SwipeRefreshLayout]
     */
    override fun onRefresh() = Unit

    private fun retryRequest() {
        showLoadingStart()
        onRefresh()
    }

    /**
     * Shows a full-screen progress indicator or sets [SwipeRefreshLayout] to refreshing, if present
     */
    protected fun showLoadingStart() {
        if (registered) {
            baseActivity.connectivityManager.unregisterNetworkCallback(networkCallback)
            registered = false
        }

        swipeRefreshLayout?.let {
            it.isRefreshing = true
            return
        }

        errorLayoutsContainer.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        progressLayout.visibility = View.VISIBLE
    }

    /**
     * Indicates that the background progress ended by hiding error and progress layout
     * and stopping the refreshing of [SwipeRefreshLayout]
     */
    protected fun showLoadingEnded() {
        errorLayoutsContainer.visibility = View.GONE
        progressLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        swipeRefreshLayout?.isRefreshing = false
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        var animation = super.onCreateAnimation(transit, enter, nextAnim)
        if (animation == null && nextAnim != 0) {
            animation = AnimationUtils.loadAnimation(activity, nextAnim)
        }

        if (animation != null) {
            view?.setLayerType(View.LAYER_TYPE_HARDWARE, null)

            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) = Unit

                override fun onAnimationEnd(animation: Animation?) {
                    view?.setLayerType(View.LAYER_TYPE_NONE, null)
                }

                override fun onAnimationRepeat(animation: Animation?) = Unit
            })
        }

        return animation
    }

    override fun onDestroyView() {
        apiCall?.cancel()
        swipeRefreshLayout = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        loadingDisposable.dispose()

        if (registered) {
            baseActivity.connectivityManager.unregisterNetworkCallback(networkCallback)
            registered = false
        }
        super.onDestroy()
    }
}
