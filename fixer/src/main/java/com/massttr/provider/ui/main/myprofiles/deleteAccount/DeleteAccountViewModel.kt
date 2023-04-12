package com.massttr.provider.ui.main.myprofiles.deleteAccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.DeleteResponse
import kotlinx.coroutines.launch

class DeleteAccountViewModel : BaseViewModel() {
    private val _delete = SingleLiveEvent<DeleteResponse>()
    val delete: LiveData<DeleteResponse> = _delete

    fun deleteProfile() {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.deleteProfile()) {
                _delete.postValue(it)
            }
        }
    }
}