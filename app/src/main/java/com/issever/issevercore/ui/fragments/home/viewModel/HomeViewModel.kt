package com.issever.issevercore.ui.fragments.home.viewModel

import androidx.lifecycle.LiveData
import com.issever.core.base.BaseViewModel
import com.issever.core.data.enums.StateType
import com.issever.core.util.SingleLiveEvent
import com.issever.issevercore.data.repository.FactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FactRepository
) : BaseViewModel() {

    private val _navigateToCatFacts = SingleLiveEvent<Boolean>()
    val navigateToCatFacts: LiveData<Boolean> get() = _navigateToCatFacts

    private val _showLanguageDialog = SingleLiveEvent<Boolean>()
    val showLanguageDialog: LiveData<Boolean> get() = _showLanguageDialog

    private val _showThemeDialog = SingleLiveEvent<Boolean>()
    val showThemeDialog: LiveData<Boolean> get() = _showThemeDialog

    // Home Fragment Sample Functions
    private fun sampleFunction(stateType: StateType, actionText: String = "Action") {
        collectData({
            // The function to be executed to fetch data from the repository
            repository.sampleFunction()
        }, successAction = {
            // Code to be executed when the data fetching is successful
            // This is typically where you would handle the successful response
        }, errorAction = { message, errorBody ->
            // Code to be executed when there is an error in fetching data
            // Here you can handle the error
        }, loadingAction = {
            // Code to be executed while the data is being loaded
        }, stateType = stateType,
            actionText = actionText,
            snackBarAction = {
                // Action to be performed when the snackbar action is triggered
                // This is typically used for actions that the user can perform directly from the snackbar, such as retrying a failed operation
            })
    }

    fun getSuccess(){
        sampleFunction(StateType.SUCCESS)
    }

    fun getError(){
        sampleFunction(StateType.ERROR)
    }

    fun getWarning(){
        sampleFunction(StateType.WARNING)
    }

    fun getInfo(){
        sampleFunction(StateType.INFO)
    }

    fun getDefault(){
        sampleFunction(StateType.DEFAULT)
    }

    fun navigateToCatFacts() {
        _navigateToCatFacts.value = true
    }

    fun chooseTheme(){
        _showThemeDialog.value = true
    }

    fun chooseLanguage(){
        _showLanguageDialog.value = true
    }
}