package com.massttr.user.ui.main.home.taskrequest.task_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.BookJobResponse
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.request.BookJob
import kotlinx.coroutines.launch

class TaskDetailsViewModel : BaseViewModel() {
    private val _bookJob = SingleLiveEvent<CommonResponse<BookJobResponse>>()
    val bookJob: LiveData<CommonResponse<BookJobResponse>> = _bookJob

    fun bookJob(book: BookJob) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.bookJob(book)) {
                _bookJob.postValue(it)
            }
        }
    }
}