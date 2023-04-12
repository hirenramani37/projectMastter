package com.massttr.user.ui.main.myprofile.order_history.order_details.cancel_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.ContactUsResponse
import com.common.data.network.model.SelectReasonResponse
import com.common.data.network.model.request.ContactUs
import com.common.data.network.model.request.JobChangeStatus
import com.common.data.network.model.request.SelectReason
import kotlinx.coroutines.launch

class CancelTaskActivityViewModel : BaseViewModel() {
    private val _selectReason = SingleLiveEvent<SelectReasonResponse>()
    val selectReason: LiveData<SelectReasonResponse> = _selectReason

    private val _jobStatus = SingleLiveEvent<Any>()
    val jobStatus: LiveData<Any> = _jobStatus

    fun selectReason(reason: SelectReason) {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.selectReason(reason)) {
                _selectReason.postValue(it)
            }
        }
    }

    fun jobStatus(jobStatus: JobChangeStatus){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.jobChangeStatus(jobStatus)) {
                _jobStatus.postValue(it.data)
            }
        }
    }
}