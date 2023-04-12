package com.massttr.provider.ui.main.myprofiles.helpCenter.faq

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponses
import com.common.data.network.model.FaqResponse
import kotlinx.coroutines.launch

class FaqViewModel : BaseViewModel() {
    private val _faq = SingleLiveEvent<CommonResponses<FaqResponse>>()
    val faq: LiveData<CommonResponses<FaqResponse>> = _faq

    fun faq() {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.faq()) {
                _faq.postValue(it)
            }
        }
    }
}