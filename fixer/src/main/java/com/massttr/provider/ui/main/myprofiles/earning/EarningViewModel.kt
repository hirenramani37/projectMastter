package com.massttr.provider.ui.main.myprofiles.earning

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.EarningResponse
import com.common.data.network.model.request.FixerEarning
import kotlinx.coroutines.launch

class EarningViewModel : BaseViewModel() {

    private val _fixerEarning = SingleLiveEvent<CommonResponse<EarningResponse>>()
    val fixerEarning: LiveData<CommonResponse<EarningResponse>> = _fixerEarning

    fun fixerEarning(earning: FixerEarning) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.fixerEarning(earning)) {
                _fixerEarning.postValue(it)
            }
        }
    }
}