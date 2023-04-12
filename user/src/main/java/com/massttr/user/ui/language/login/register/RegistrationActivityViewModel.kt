package com.massttr.user.ui.language.login.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.RegistrationResponse
import com.common.data.network.model.request.Registration
import kotlinx.coroutines.launch

class RegistrationActivityViewModel : BaseViewModel() {
    private val _register = SingleLiveEvent<CommonResponse<RegistrationResponse>>()
    val register : LiveData<CommonResponse<RegistrationResponse>> = _register

    fun register(register: Registration){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.register(register)){
                _register.postValue(it)
            }
        }
    }
}