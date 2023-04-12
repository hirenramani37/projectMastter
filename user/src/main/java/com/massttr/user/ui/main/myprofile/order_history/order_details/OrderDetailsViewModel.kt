package com.massttr.user.ui.main.myprofile.order_history.order_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.JobDetailsResponse
import com.common.data.network.model.request.CompleteTaskStatus
import com.common.data.network.model.request.JobDetails
import kotlinx.coroutines.launch

class OrderDetailsViewModel : BaseViewModel() {
    private val _jobDetails = SingleLiveEvent<CommonResponse<JobDetailsResponse>>()
    val jobDetails: LiveData<CommonResponse<JobDetailsResponse>> = _jobDetails

    private val _jobStatus = SingleLiveEvent<Any>()
    val jobStatus: LiveData<Any> = _jobStatus

    fun jobDetails(job: JobDetails) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.jobDetails(job)) {
                _jobDetails.postValue(it)
            }
        }
    }

    fun jobStatus(jobStatus: CompleteTaskStatus){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.jobStatusCompleteTask(jobStatus)) {
                _jobStatus.postValue(it.data)
            }
        }
    }
}