package com.massttr.provider.ui.main.myprofiles.editProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.*
import com.common.data.network.model.request.City
import com.common.data.network.model.request.UserProfileUpdate
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class EditProfileViewModel : BaseViewModel() {

    private val _getProfile = SingleLiveEvent<UserInfo>()
    val getProfile: LiveData<UserInfo> = _getProfile

    private val _editProfile = SingleLiveEvent<CommonResponse<ProfileUpdate>>()
    val editProfile: LiveData<CommonResponse<ProfileUpdate>> = _editProfile

    private val _state = SingleLiveEvent<List<States>>()
    val state : LiveData<List<States>> = _state

    private val _city = SingleLiveEvent<List<Cities>>()
    val city : LiveData<List<Cities>> = _city

    private val _uploadProfileImage = SingleLiveEvent<CommonUploadImageResponse>()
    val uploadProfileImage: LiveData<CommonUploadImageResponse> = _uploadProfileImage

    fun editProfile(userProfileUpdate: UserProfileUpdate){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.updateProfile(userProfileUpdate)) {
                _editProfile.postValue(it)
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

    fun city(city: City){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.city(city)){
                _city.postValue(it.data)
            }
        }
    }

    fun state(){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.state()){
                _state.postValue(it.data)
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