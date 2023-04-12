package com.massttr.user.ui.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponses
import com.common.data.network.model.LanguageResponse
import com.common.data.network.model.request.Language
import kotlinx.coroutines.launch
/**
 * created by Chirag Variya on 30/09/21
 */
class LanguageActivityViewModel : BaseViewModel() {
    private val _language = SingleLiveEvent<CommonResponses<LanguageResponse>>()
    val language: LiveData<CommonResponses<LanguageResponse>> = _language

    fun language(lang: Language) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.language(lang)) {
                _language.postValue(it)
            }
        }
    }
}