package com.massttr.provider.ui.main.myUpcomingFix

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.MyFix
import com.common.data.network.model.UpComingFixResponse
import kotlinx.coroutines.launch

class MyUpcomingFixViewModel : BaseViewModel() {
    private val _fixList = SingleLiveEvent<MyFix>()
    val fixList: LiveData<MyFix> = _fixList

    fun myFixList(currentPage:Int) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.fixList(currentPage)) {
                _fixList.postValue(it.data)
            }
        }
    }
}
