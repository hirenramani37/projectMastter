package com.massttr.provider.ui.main.myUpcomingFix.cancelTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponses
import com.common.data.network.model.ContactUsResponse
import com.common.data.network.model.SelectReasonResponse
import com.common.data.network.model.request.ContactUs
import com.common.data.network.model.request.JobChangeStatus
import com.common.data.network.model.request.JobStatus
import com.common.data.network.model.request.SelectReason
import kotlinx.coroutines.launch

class CancelTasksActivityViewModel : BaseViewModel(){

    private val _selectReason = SingleLiveEvent<CommonResponses<SelectReasonResponse>>()
    val selectReason: LiveData<CommonResponses<SelectReasonResponse>> = _selectReason

    private val _changeJob = SingleLiveEvent<Any>()
    val changeJobStatus: LiveData<Any> = _changeJob

    fun selectReason(reason: SelectReason) {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.selectReason(reason)) {
                _selectReason.postValue(it)
            }
        }
    }

    fun cancelStatus(jobStatus: JobChangeStatus){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.changeJobStatus2(jobStatus)) {
                _changeJob.postValue(it.data)
            }
        }
    }
}
