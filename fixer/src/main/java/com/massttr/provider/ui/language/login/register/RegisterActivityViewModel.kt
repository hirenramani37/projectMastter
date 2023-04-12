package com.massttr.provider.ui.language.login.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.RegisterResponse
import com.common.data.network.model.request.CreateRegistration
import kotlinx.coroutines.launch

class RegisterActivityViewModel : BaseViewModel() {
    private val _register = SingleLiveEvent<CommonResponse<RegisterResponse>>()
    val register: LiveData<CommonResponse<RegisterResponse>> = _register

    fun register(register: CreateRegistration) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.register(register)) {
                _register.postValue(it)
            }
        }
    }
}