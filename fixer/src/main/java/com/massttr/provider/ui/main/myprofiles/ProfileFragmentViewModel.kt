package com.massttr.provider.ui.main.myprofiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.LanguageResponse
import com.common.data.network.model.LogoutResponse
import com.common.data.network.model.StatusCheckResponse
import com.common.data.network.model.request.Language
import com.common.data.network.model.request.StatusCheck
import kotlinx.coroutines.launch

class ProfileFragmentViewModel : BaseViewModel() {
    private val _logout = SingleLiveEvent<LogoutResponse>()
    val logout: LiveData<LogoutResponse> = _logout

    private val _language = SingleLiveEvent<LanguageResponse>()
    val language: LiveData<LanguageResponse> = _language

    private val _status = SingleLiveEvent<CommonResponse<StatusCheckResponse>>()
    val status: LiveData<CommonResponse<StatusCheckResponse>> = _status

    fun logout() {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.logout()) {
                _logout.postValue(it)
            }
        }
    }

    fun language(lang: Language) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.language(lang)) {
                _language.postValue(it)
            }
        }
    }

    fun statusCheck(status: StatusCheck) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.statusCheck(status)) {
                _status.postValue(it)
            }
        }
    }
}
