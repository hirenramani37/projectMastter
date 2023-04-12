package com.massttr.provider.ui.main.myprofiles.helpCenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.*
import kotlinx.coroutines.launch

class HelpCenterViewModel : BaseViewModel() {
    private val _helpCenter = SingleLiveEvent<CommonResponses<PrivacyPolicyResponse>>()
    val helpCenter: LiveData<CommonResponses<PrivacyPolicyResponse>> = _helpCenter

    fun helpCenter() {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.helpCenter()) {
                _helpCenter.postValue(it)
            }
        }
    }
}