package com.massttr.user.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponses
import com.common.data.network.model.EditProfileResponse
import com.common.data.network.model.HomeMapGetFixerResponse
import com.common.data.network.model.request.HomeMapGetFixer
import kotlinx.coroutines.launch

class HomeFragmentViewModel : BaseViewModel() {
    private val _mapGetFixer = SingleLiveEvent<CommonResponses<HomeMapGetFixerResponse>>()
    val mapGetFixer: LiveData<CommonResponses<HomeMapGetFixerResponse>> = _mapGetFixer

    private val _editProfile = SingleLiveEvent<EditProfileResponse>()
    val editProfile: LiveData<EditProfileResponse> = _editProfile

    fun editProfile() {
        viewModelScope.launch {
           // displayLoader()
            processDataEvent(api1Repository.editProfile()) {
                _editProfile.postValue(it)
            }
        }
    }

    fun getFixerMap(map: HomeMapGetFixer) {
        //displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.mapGetFixer(map)) {
                _mapGetFixer.postValue(it)
            }
        }
    }
}