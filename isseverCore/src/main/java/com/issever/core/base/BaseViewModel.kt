package com.issever.core.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.issever.core.data.enums.SnackbarType
import com.issever.core.data.enums.ResourceStatus
import com.issever.core.data.model.SnackbarMessage
import com.issever.core.util.CoreErrors.COMMON_ERROR
import com.issever.core.data.initialization.IsseverCore
import com.issever.core.util.Resource
import com.issever.core.util.ResourceProvider
import com.issever.core.util.SingleLiveEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    // Lazy-initialized properties for local data and resource provider
    open val coreLocalData: BaseLocalData by lazy { IsseverCore.getBaseLocalData() }
    open val resourceProvider: ResourceProvider by lazy { ResourceProvider }

    // LiveData to observe loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // SingleLiveEvent to show Snackbar messages
    private val _snackbarText = SingleLiveEvent<SnackbarMessage>()
    val snackbarText: LiveData<SnackbarMessage>
        get() = _snackbarText

    // SingleLiveEvent to handle navigation actions
    private val _navigateBack = SingleLiveEvent<Boolean>()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack

    /**
     * Shows a loading indicator.
     * Override this function in a subclass to provide a custom loading indicator.
     */
    open fun showProgress() {
        _isLoading.postValue(true)
    }

    /**
     * Hides the loading indicator.
     * Override this function in a subclass to provide a custom method for hiding the loading indicator.
     */
    open fun hideProgress() {
        _isLoading.postValue(false)
    }

    /**
     * Displays a Snackbar message.
     * Override this function in a subclass to provide a custom Snackbar message.
     *
     * @param snackbarMessage The SnackbarMessage object containing message, type, action text, and action.
     */
    open fun showSnackbar(snackbarMessage: SnackbarMessage) {
        _snackbarText.postValue(snackbarMessage)
    }

    /**
     * Displays a success Snackbar message with an optional action.
     *
     * @param message The message to display.
     * @param actionText Optional text for the action button.
     * @param action Optional action to perform when the action button is clicked.
     */
    open fun showSuccessSnackbar(message: String, actionText: String? = null, action: (() -> Unit)? = null) {
        showSnackbar(SnackbarMessage(message, SnackbarType.SUCCESS, actionText = actionText, action = action))
    }

    /**
     * Displays a success Snackbar message with an optional action using string resource IDs.
     *
     * @param messageResId The resource ID of the message to display.
     * @param actionTextResId Optional resource ID for the action button text.
     * @param action Optional action to perform when the action button is clicked.
     */
    open fun showSuccessSnackbar(@StringRes messageResId: Int, @StringRes actionTextResId: Int? = null, action: (() -> Unit)? = null) {
        showSnackbar(SnackbarMessage(resourceProvider.getString(messageResId), SnackbarType.SUCCESS, actionText = actionTextResId?.let {
            resourceProvider.getString(it)
        }, action = action))
    }

    /**
     * Displays an error Snackbar message with an optional action.
     *
     * @param message The message to display.
     * @param actionText Optional text for the action button.
     * @param action Optional action to perform when the action button is clicked.
     */
    open fun showErrorSnackbar(message: String, actionText: String? = null, action: (() -> Unit)? = null) {
        showSnackbar(SnackbarMessage(message, SnackbarType.ERROR, actionText = actionText, action = action))
    }

    /**
     * Displays an error Snackbar message with an optional action using string resource IDs.
     *
     * @param messageResId The resource ID of the message to display.
     * @param actionTextResId Optional resource ID for the action button text.
     * @param action Optional action to perform when the action button is clicked.
     */
    open fun showErrorSnackbar(@StringRes messageResId: Int, @StringRes actionTextResId: Int? = null, action: (() -> Unit)? = null) {
        showSnackbar(SnackbarMessage(resourceProvider.getString(messageResId), SnackbarType.ERROR, actionText = actionTextResId?.let {
            resourceProvider.getString(it)
        }, action = action))
    }

    /**
     * Collects data from a Flow and handles success, error, loading states, and shows corresponding Snackbar messages.
     * Override this function to customize data collection and handling.
     *
     * @param T The type of the data being collected.
     * @param operation The suspend function returning a Flow of Resource.
     * @param successAction Optional action to perform on success.
     * @param errorAction Optional action to perform on error. Takes a message and an optional error body.
     * @param loadingAction Optional action to perform while loading.
     * @param snackbarType Optional type of Snackbar to show on error or other custom state.
     * @param actionText Optional text for the Snackbar action button.
     * @param snackBarAction Optional action to perform when the Snackbar action button is clicked.
     */
    open fun <T> collectData(
        operation: suspend () -> Flow<Resource<T>>,
        successAction: ((T?) -> Unit)? = null,
        errorAction: ((String, String?) -> Unit)? = null,
        loadingAction: (() -> Unit)? = null,
        snackbarType: SnackbarType? = null,
        actionText: String? = null,
        snackBarAction: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            operation().collect {
                when (it.status) {
                    ResourceStatus.SUCCESS -> {
                        hideProgress()
                        successAction?.invoke(it.data)
                        it.message?.let { message ->
                            showSnackbar(
                                SnackbarMessage(
                                    message,
                                    SnackbarType.SUCCESS,
                                    action = snackBarAction,
                                    actionText = actionText
                                )
                            )
                        }
                    }

                    ResourceStatus.ERROR -> {
                        hideProgress()
                        it.message?.let { message ->
                            showSnackbar(
                                SnackbarMessage(
                                    message,
                                    snackbarType ?: SnackbarType.ERROR,
                                    action = snackBarAction,
                                    actionText = actionText
                                )
                            )
                        }
                        errorAction?.invoke(it.message ?: COMMON_ERROR,it.errorBody)
                    }

                    ResourceStatus.WARNING -> {
                        hideProgress()
                        successAction?.invoke(it.data)
                        it.message?.let { message ->
                            showSnackbar(
                                SnackbarMessage(
                                    message,
                                    snackbarType ?: SnackbarType.WARNING,
                                    action = snackBarAction,
                                    actionText = actionText
                                )
                            )
                        }
                        errorAction?.invoke(it.message ?: COMMON_ERROR,it.errorBody)
                    }

                    ResourceStatus.INFO -> {
                        hideProgress()
                        successAction?.invoke(it.data)
                        it.message?.let { message ->
                            showSnackbar(
                                SnackbarMessage(
                                    message,
                                    SnackbarType.INFO,
                                    action = snackBarAction,
                                    actionText = actionText
                                )
                            )
                        }
                    }

                    ResourceStatus.LOADING -> {
                        showProgress()
                        loadingAction?.invoke()
                    }
                }
            }
        }
    }
}
