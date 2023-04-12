package com.massttr.provider.ui.main.availableTasks.viewTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.ViewTaskResponse
import com.common.data.network.model.request.JobInterested
import com.common.data.network.model.request.JobStatus
import com.common.data.network.model.request.ViewTaskAccept
import kotlinx.coroutines.launch

class ViewTaskViewModel : BaseViewModel() {
    private val _viewTask = SingleLiveEvent<ViewTaskResponse>()
    val viewTask: LiveData<ViewTaskResponse> = _viewTask

    private val _interestedJob = SingleLiveEvent<Any>()
    val interested: LiveData<Any> = _interestedJob

    private val _changeJob = SingleLiveEvent<Any>()
    val changeJobStatus: LiveData<Any> = _changeJob

    fun interestedJob(jobInterested: JobInterested){
        viewModelScope.launch {
            dismissLoader()
            processDataEvent(api1Repository.interestedJob(jobInterested)){
                _interestedJob.postValue(it.data)
            }
        }
    }

    fun viewTask(viewTask: ViewTaskAccept) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.viewTask(viewTask)) {
                _viewTask.postValue(it.data)
            }
        }
    }

    fun endOrStartTask(jobStatus: JobStatus){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.changeJobStatus(jobStatus)) {
                _changeJob.postValue(it.data)
            }
        }
    }
}
