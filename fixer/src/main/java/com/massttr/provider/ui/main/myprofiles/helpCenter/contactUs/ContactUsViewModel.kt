package com.massttr.provider.ui.main.myprofiles.helpCenter.contactUs

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.*
import com.common.data.network.model.request.ContactUs
import com.common.data.network.model.request.SelectReason
import kotlinx.coroutines.launch

class ContactUsViewModel : BaseViewModel() {
    private val _contact = SingleLiveEvent<CommonResponses<ContactUsResponse>>()
    val contact: LiveData<CommonResponses<ContactUsResponse>> = _contact

    private val _reason = SingleLiveEvent<CommonResponses<SelectReasonResponse>>()
    val reason: LiveData<CommonResponses<SelectReasonResponse>> = _reason

    private val _settings = SingleLiveEvent<CommonResponse<GetSettings>>()
    val settings: LiveData<CommonResponse<GetSettings>> = _settings

    fun contactUs(contactUs: ContactUs) {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.contactUs(contactUs)) {
                _contact.postValue(it)
            }
        }
    }

    fun selectReason(reason: SelectReason) {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.selectReason(reason)) {
                _reason.postValue(it)
            }
        }
    }

    fun settings() {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.getSetting()) {
                _settings.postValue(it)
            }
        }
    }
}