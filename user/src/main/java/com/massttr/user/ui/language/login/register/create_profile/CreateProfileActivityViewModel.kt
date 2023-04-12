package com.massttr.user.ui.language.login.register.create_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.CreateProfileResponse
import com.common.data.network.model.UploadJobImageResponse
import com.common.data.network.model.UserInfo
import com.common.data.network.model.request.CreateProfile
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class CreateProfileActivityViewModel : BaseViewModel() {
    private val _create = SingleLiveEvent<CommonResponse<UserInfo>>()
    val create: LiveData<CommonResponse<UserInfo>> = _create

    private val _uploadProfileImage = SingleLiveEvent<UploadJobImageResponse>()
    val uploadProfileImage: LiveData<UploadJobImageResponse> = _uploadProfileImage

    fun uploadJobImage(multipartBody: MultipartBody) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.uploadJobImage(multipartBody)) {
                _uploadProfileImage.postValue(it)
            }
        }
    }

    fun createProfile(crtProfile: CreateProfile) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.createProfile(crtProfile)) {
                _create.postValue(it)
            }
        }
    }
}