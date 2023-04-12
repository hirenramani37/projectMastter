package com.massttr.user.ui.main.myprofile.delete_account

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.DeleteResponse
import kotlinx.coroutines.launch

/**
 * created by Manisha on 05/10/21
 */
class DeleteAccountViewModel : BaseViewModel() {
    private val _delete = SingleLiveEvent<DeleteResponse>()
    val delete: LiveData<DeleteResponse> = _delete

    fun delete() {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.delete()) {
                _delete.postValue(it)
            }
        }
    }
}