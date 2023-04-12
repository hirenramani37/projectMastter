package com.massttr.user.ui.main.myprofile.help_center.privacy_policy

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.PrivacyPolicyResponse
import kotlinx.coroutines.launch

/**
 * created by Chirag Variya on 05/10/21
 */
class PrivacyPolicyViewModel : BaseViewModel() {
    private val _privacyPolicy = SingleLiveEvent<PrivacyPolicyResponse>()
    val privacyPolicy: LiveData<PrivacyPolicyResponse> = _privacyPolicy

    fun privacyPolicy() {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.privacyPolicy()) {
                _privacyPolicy.postValue(it)
            }
        }
    }
}