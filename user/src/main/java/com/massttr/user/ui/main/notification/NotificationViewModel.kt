package com.massttr.user.ui.main.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponses
import com.common.data.network.model.NotificationResponse
import kotlinx.coroutines.launch

class NotificationViewModel : BaseViewModel() {
    private val _notification = SingleLiveEvent<CommonResponses<NotificationResponse>>()
    val notification: LiveData<CommonResponses<NotificationResponse>> = _notification

    fun notificationList() {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.notification()) {
                _notification.postValue(it)
            }
        }
    }
}