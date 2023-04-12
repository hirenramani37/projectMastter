package com.massttr.user.ui.main.myprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponses
import com.common.data.network.model.LanguageResponse
import com.common.data.network.model.LogoutResponse
import com.common.data.network.model.request.Language
import kotlinx.coroutines.launch

/**
 * created by Chirag Variya on 05/10/21
 */
class MyProfileViewModel : BaseViewModel() {
    private val _logout = SingleLiveEvent<LogoutResponse>()
    val logout: LiveData<LogoutResponse> = _logout

    private val _language = SingleLiveEvent<CommonResponses<LanguageResponse>>()
    val language: LiveData<CommonResponses<LanguageResponse>> = _language

    fun logout() {
        viewModelScope.launch {
            displayLoader()
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
}