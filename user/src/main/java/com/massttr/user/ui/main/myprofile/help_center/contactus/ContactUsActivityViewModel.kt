package com.massttr.user.ui.main.myprofile.help_center.contactus

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.ContactUsResponse
import com.common.data.network.model.SelectReasonResponse
import com.common.data.network.model.SettingsResponse
import com.common.data.network.model.request.ContactUs
import com.common.data.network.model.request.SelectReason
import kotlinx.coroutines.launch

/**
 * created by Chirag Variya on 05/10/21
 */
class ContactUsActivityViewModel : BaseViewModel() {
    private val _contactUs = SingleLiveEvent<ContactUsResponse>()
    val contactUs: LiveData<ContactUsResponse> = _contactUs

    private val _selectReason = SingleLiveEvent<SelectReasonResponse>()
    val selectReason: LiveData<SelectReasonResponse> = _selectReason

    private val _settings = SingleLiveEvent<CommonResponse<SettingsResponse>>()
    val settings: LiveData<CommonResponse<SettingsResponse>> = _settings

    fun contactUs(contact: ContactUs) {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.contactUs(contact)) {
                _contactUs.postValue(it)
            }
        }
    }

    fun selectReason(reason: SelectReason) {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.selectReason(reason)) {
                _selectReason.postValue(it)
            }
        }
    }

    fun settings(){
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.settings()){
                _settings.postValue(it)
            }
        }
    }
}