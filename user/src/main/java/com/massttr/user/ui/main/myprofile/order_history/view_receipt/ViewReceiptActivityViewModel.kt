package com.massttr.user.ui.main.myprofile.order_history.view_receipt

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.BookFixerResponse
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.CommonResponses
import com.common.data.network.model.JobDetailsResponse
import com.common.data.network.model.request.BookFixer
import com.common.data.network.model.request.JobDetails
import kotlinx.coroutines.launch

class ViewReceiptActivityViewModel : BaseViewModel() {

    private val _jobId = SingleLiveEvent<CommonResponse<JobDetailsResponse>>()
    val jobId: LiveData<CommonResponse<JobDetailsResponse>> = _jobId

    fun jobId(job: JobDetails) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.jobDetails(job)) {
                _jobId.postValue(it)
            }
        }
    }
}