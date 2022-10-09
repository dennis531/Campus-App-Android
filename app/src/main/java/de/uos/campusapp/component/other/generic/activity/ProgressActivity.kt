package de.uos.campusapp.component.other.generic.activity

import android.graphics.Color
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import de.uos.campusapp.R
import de.uos.campusapp.api.general.exception.*
import de.uos.campusapp.component.other.generic.viewstates.*
import de.uos.campusapp.utils.*
import de.uos.campusapp.utils.NetUtils.internetCapability
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.connectivityManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException
import java.util.concurrent.Callable

/**
 * Generic class which handles can handle a long running background task
 *
 * @param T The type of object that is to be retrieved via the [apiCall]
 */
@Deprecated("Use BaseActivity and a suitable BaseFragment class")
abstract class ProgressActivity<T>(
    layoutId: Int,
    component: Component? = null
) : BaseActivity(layoutId, component), SwipeRefreshLayout.OnRefreshListener {

    private val loadingDisposable = CompositeDisposable()

    private var apiCall: Call<T>? = null
    private var hadSuccessfulRequest = false

    private val contentView: ViewGroup
        get() =  findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup

    protected var swipeRefreshLayout: SwipeRefreshLayout? = null

    private val errorLayoutsContainer: FrameLayout by lazy {
        findViewById<FrameLayout>(R.id.errors_layout)
    }

    private val errorLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.error_layout)
    }

    private val errorIconImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.iconImageView)
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
        findViewById<FrameLayout>(R.id.progress_layout)
    }

    private var registered: Boolean = false

    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            runOnUiThread(this@ProgressActivity::onRefresh)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        // If content is refreshable setup the SwipeRefreshLayout
        swipeRefreshLayout?.apply {
            setOnRefreshListener(this@ProgressActivity)
            setColorSchemeResources(
                    R.color.color_primary,
                    R.color.tum_A100,
                    R.color.tum_A200
            )
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
        showErrorSnackbar(R.string.error_unknown)
    }

    /**
     * Called when an Exception is raised during an API call. Displays an error layout.
     * @param throwable The error that has occurred
     */
    open fun onDownloadFailure(throwable: Throwable) {
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
            is UnauthorizedException -> showFailedLMSLayout(R.string.error_unauthorized)
            is ForbiddenException -> showFailedLMSLayout(R.string.error_no_rights_to_access_function)
            is NotFoundException -> showFailedLMSLayout(R.string.error_resource_not_found)
            else -> showError(R.string.error_unknown)
        }
    }

    private fun showFailedLMSLayout(messageResId: Int = R.string.error_accessing_api_body) {
        runOnUiThread {
            showError(FailedApiViewState(messageResId))
        }
    }

    protected fun showNoInternetLayout() {
        runOnUiThread {
            showError(NoInternetViewState())
        }

        val request = NetworkRequest.Builder()
                .addCapability(internetCapability)
                .build()

        if (registered.not()) {
            connectivityManager.registerNetworkCallback(request, networkCallback)
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
     * Shows progress layout or sets [SwipeRefreshLayout]'s state to refreshing
     * if present in the xml layout
     */
    protected fun showLoadingStart() {
        if (registered) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
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
     * and setting [SwipeRefreshLayout]'s state to completed
     */
    protected fun showLoadingEnded() {
        errorLayoutsContainer.visibility = View.GONE
        progressLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        swipeRefreshLayout?.isRefreshing = false
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

    override fun onDestroy() {
        super.onDestroy()
        loadingDisposable.dispose()
        apiCall?.cancel()
        swipeRefreshLayout = null
        if (registered) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            registered = false
        }
    }
}
