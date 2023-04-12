package com.massttr.provider.ui.main.availableTasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.*
import com.common.data.network.model.request.AvailableTask
import com.common.data.network.model.request.FixerLocation
import kotlinx.coroutines.launch

class AvailableTaskViewModel : BaseViewModel() {
    private val _availableTask = SingleLiveEvent<CommonResponse<AvailableTaskResponse>>()
    val availableTask: LiveData<CommonResponse<AvailableTaskResponse>> = _availableTask

    private val _nearByJob = SingleLiveEvent<CommonResponses<NearByJobsResponse>>()
    val nearByJob : LiveData<CommonResponses<NearByJobsResponse>> = _nearByJob

    private val _fixerLocation = SingleLiveEvent<FixerLocationResponse>()
    val fixerLocation: LiveData<FixerLocationResponse> = _fixerLocation

    private val _getProfile = SingleLiveEvent<UserInfo>()
    val getProfile: LiveData<UserInfo> = _getProfile

    fun availableTask(availableTask: AvailableTask,currentPage:Int) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.availableTask(availableTask,currentPage)) {
                _availableTask.postValue(it)
            }
        }
    }

    fun nearByJobs(availableTask: AvailableTask){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.nearJobs(availableTask)){
                _nearByJob.postValue(it)
            }
        }

    }

    fun getProfile() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.getProfile()) {
                _getProfile.postValue(it.data)
            }
        }
    }

    fun fixerLocation(fixerLocation: FixerLocation) {
        viewModelScope.launch {
           // displayLoader()
            processDataEvent(api1Repository.fixerLocation(fixerLocation)) {
                _fixerLocation.postValue(it.data)
            }
        }
    }
}
