package com.massttr.user.ui.main.myprofile.edit_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.EditProfileResponse
import com.common.data.network.model.UploadJobImageResponse
import com.common.data.network.model.UserInfo
import com.common.data.network.model.request.UpdateProfile
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class EditProfileActivityViewModel : BaseViewModel() {
    private val _editProfile = SingleLiveEvent<EditProfileResponse>()
    val editProfile: LiveData<EditProfileResponse> = _editProfile

    private val _updateProfile = SingleLiveEvent<CommonResponse<UserInfo>>()
    val updateProfile: LiveData<CommonResponse<UserInfo>> = _updateProfile

    private val _uploadProfileImage = SingleLiveEvent<UploadJobImageResponse>()
    val uploadProfileImage: LiveData<UploadJobImageResponse> = _uploadProfileImage

    fun editProfile() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.editProfile()) {
                _editProfile.postValue(it)
            }
        }
    }

    fun updateProfile(updateProfile: UpdateProfile) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.updateProfile(updateProfile)) {
                _updateProfile.postValue(it)
            }
        }
    }

    fun uploadJobImage(multipartBody: MultipartBody) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.uploadJobImage(multipartBody)) {
                _uploadProfileImage.postValue(it)
            }
        }
    }
}