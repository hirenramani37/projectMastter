package com.massttr.user.ui.main.home.taskrequest

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.*
import com.common.data.network.model.request.BookJob
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class TaskRequestActivityViewModel : BaseViewModel() {
    private val _category = SingleLiveEvent<CommonResponses<CategoryResponse>>()
    val category: LiveData<CommonResponses<CategoryResponse>> = _category

    private val _uploadJobImageMultiple = SingleLiveEvent<UploadJobImageResponse>()
    val uploadJobImageMultiple: LiveData<UploadJobImageResponse> = _uploadJobImageMultiple

    private val _uploadBannerImage = SingleLiveEvent<UploadJobImageResponse>()
    val uploadBannerImage: LiveData<UploadJobImageResponse> = _uploadBannerImage

    fun category() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.category()) {
                _category.postValue(it)
            }
        }
    }

    fun uploadJobBanner(multipartBody: MultipartBody) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.uploadJobImageMultiple(multipartBody)) {
                _uploadBannerImage.postValue(it)
            }
        }
    }

    fun uploadJobImageMultiple(multipartBody: MultipartBody) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.uploadJobImageMultiple(multipartBody)) {
                _uploadJobImageMultiple.postValue(it)
            }
        }
    }
}