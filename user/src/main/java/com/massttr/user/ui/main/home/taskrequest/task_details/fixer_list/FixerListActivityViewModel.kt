package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.NearBy
import com.common.data.network.model.request.NearBrFixer
import com.common.data.network.model.request.NearBrFixerPopUp

import kotlinx.coroutines.launch

class FixerListActivityViewModel : BaseViewModel() {
    private val _nearByFixer = SingleLiveEvent<NearBy>()
    val nearByFixer: LiveData<NearBy> = _nearByFixer

    private val _nearByFixerPopUp = SingleLiveEvent<NearBy>()
    val nearByFixerPopUp: LiveData<NearBy> = _nearByFixerPopUp

    fun nearByFixer(near: NearBrFixer) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.nearByFixers(near)) {
                _nearByFixer.postValue(it.data)
            }
        }
    }

    fun nearByFixerPopUp(near: NearBrFixerPopUp) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.nearByFixersPopUp(near)) {
                _nearByFixerPopUp.postValue(it.data)
            }
        }
    }
}
