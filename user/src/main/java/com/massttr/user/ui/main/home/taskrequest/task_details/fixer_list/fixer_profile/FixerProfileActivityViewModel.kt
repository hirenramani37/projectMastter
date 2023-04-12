package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.BookFixerResponse
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.CommonResponses
import com.common.data.network.model.FixerDetailsResponse
import com.common.data.network.model.request.BookFixer
import com.common.data.network.model.request.FixerProfile
import kotlinx.coroutines.launch

class FixerProfileActivityViewModel : BaseViewModel() {
    private val _fixerProfile = SingleLiveEvent<CommonResponse<FixerDetailsResponse>>()
    val fixerProfile: LiveData<CommonResponse<FixerDetailsResponse>> = _fixerProfile

    private val _bookFixer = SingleLiveEvent<CommonResponses<BookFixerResponse>>()
    val bookFixer: LiveData<CommonResponses<BookFixerResponse>> = _bookFixer

    fun fixerProfile(fixer: FixerProfile) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.fixerProfile(fixer)) {
                _fixerProfile.postValue(it)
            }
        }
    }

    fun bookFixer(book: BookFixer) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.bookFixer(book)) {
                _bookFixer.postValue(it)
            }
        }
    }
}
