package com.issever.issevercore.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.issever.core.base.BaseViewModel
import com.issever.core.util.SingleLiveEvent
import com.issever.issevercore.data.model.CatFact
import com.issever.issevercore.data.repository.FactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FactRepository
) : BaseViewModel() {

    private val _facts = MutableLiveData<List<CatFact>>()
    val facts: LiveData<List<CatFact>> get() = _facts

    private val _showFavoriteFact = SingleLiveEvent<CatFact>()
    val showFavoriteFact: LiveData<CatFact> get() = _showFavoriteFact

    fun getCatFacts() {
        collectData({
            repository.getFacts()
        }, {
            _facts.value = it?.data
        })
    }

    fun getFavoriteCatFact() {
        collectData({
            repository.getFavoriteCatFact()
        }, {
            _showFavoriteFact.value = it
        })
    }

    fun setFavoriteCatFact(catFact: CatFact) {
        collectData({
            repository.setFavoriteCatFact(catFact)
        })
    }
}