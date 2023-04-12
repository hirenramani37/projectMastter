package com.massttr.user.ui.main.inbox.chat.edit_price

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponses
import com.common.data.network.model.request.ChangePrice
import kotlinx.coroutines.launch

class EditTaskPriceActivityViewModel : BaseViewModel() {
    private val _changePrice = SingleLiveEvent<CommonResponses<Any>>()
    val changePrice: LiveData<CommonResponses<Any>> = _changePrice

    fun changePrice(change: ChangePrice) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.changePrice(change)) {
                _changePrice.postValue(it)
            }
        }
    }
}