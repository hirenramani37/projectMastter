package com.massttr.provider.ui.language.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.RegisterResponse
import com.common.data.network.model.request.CreateRegistration
import com.common.data.network.model.request.Login
import kotlinx.coroutines.launch

class LoginActivityViewModel : BaseViewModel() {
    private val _login = SingleLiveEvent<CommonResponse<RegisterResponse>>()
    val login: LiveData<CommonResponse<RegisterResponse>> = _login

    fun login(login: Login) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.login(login)) {
                _login.postValue(it)
            }
        }
    }

    fun registration(createRegistration: CreateRegistration){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.register(createRegistration)) {
                _login.postValue(it)
            }
        }
    }
}