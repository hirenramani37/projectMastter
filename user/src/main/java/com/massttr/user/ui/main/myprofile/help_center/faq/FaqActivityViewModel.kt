package com.massttr.user.ui.main.myprofile.help_center.faq

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.FaqResponse
import kotlinx.coroutines.launch

/**
 * created by Chirag Variya on 05/10/21
 */
class FaqActivityViewModel : BaseViewModel() {
    private val _faq = SingleLiveEvent<FaqResponse>()
    val faq: LiveData<FaqResponse> = _faq

    fun faq() {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.faq()) {
                _faq.postValue(it)
            }
        }
    }
}