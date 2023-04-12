package com.massttr.provider.ui.main.myprofiles.completedTasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonUploadImageResponse
import com.common.data.network.model.CompleteJob
import com.common.data.network.model.JobImage
import com.common.data.network.model.request.CompletedJob
import com.common.data.network.model.request.DeleteJobPhoto
import com.common.data.network.model.request.JobStatus
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class CompletedTaskViewModel : BaseViewModel() {
    private val _job = SingleLiveEvent<CompleteJob>()
    val job: LiveData<CompleteJob> = _job

    private val _afterImage = SingleLiveEvent<List<JobImage>>()
    val afterImage: LiveData<List<JobImage>> = _afterImage

    private val _beforeImage = SingleLiveEvent<List<JobImage>>()
    val beforeImage: LiveData<List<JobImage>> = _beforeImage

    private val _jobUpload = SingleLiveEvent<List<JobImage>>()
    val jobUpload: LiveData<List<JobImage>> = _jobUpload

    private val _changeJob = SingleLiveEvent<Any>()
    val changeJobStatus: LiveData<Any> = _changeJob

    private val _uploadProfileImage = SingleLiveEvent<CommonUploadImageResponse>()
    val uploadProfileImage: LiveData<CommonUploadImageResponse> = _uploadProfileImage

    private val _bookJobPhotoDelete = SingleLiveEvent<Any>()
    val bookJobPhotoDelete: LiveData<Any> = _bookJobPhotoDelete


    fun jobUpload(multipartBody: MultipartBody) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.uploadImage(multipartBody)) {
                _jobUpload.postValue(it.data)
            }
        }
    }

    fun completedTask(currentPage:Int) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.completedJob(currentPage)) {
                _job.postValue(it.data)
            }
        }
    }

    fun getAfterJobImage(jobImage: com.common.data.network.model.request.JobImage){
        viewModelScope.launch {
            dismissLoader()
            processDataEvent(api1Repository.getTaskImage(jobImage)) {
                _afterImage.postValue(it.data)
            }
        }
    }

    fun getBeforeJobImage(jobImage: com.common.data.network.model.request.JobImage){
        viewModelScope.launch {
            dismissLoader()
            processDataEvent(api1Repository.getTaskImage(jobImage)) {
                _beforeImage.postValue(it.data)
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

    //common
    fun uploadJobImage(multipartBody: MultipartBody) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.uploadJobImage(multipartBody)) {
                _uploadProfileImage.postValue(it)
            }
        }
    }

    fun deleteJobImage(deleteJobPhoto: DeleteJobPhoto){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.deleteJobImage(deleteJobPhoto)){
                _bookJobPhotoDelete.postValue(it)
            }
        }
    }
}
