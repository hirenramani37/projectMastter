package com.massttr.user.ui.main.myprofile.order_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.OrderHistoryResponse
import kotlinx.coroutines.launch


class OrderHistoryViewModel : BaseViewModel() {
    private val _orderHistory = SingleLiveEvent<OrderHistoryResponse>()
    val orderHistory: LiveData<OrderHistoryResponse> = _orderHistory

    fun orderHistory(currentPage:Int) {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.orderHistory(currentPage)) {
                _orderHistory.postValue(it.data)
            }
        }
    }
}