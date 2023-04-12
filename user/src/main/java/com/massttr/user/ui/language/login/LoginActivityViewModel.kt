package com.massttr.user.ui.language.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.LoginResponse
import com.common.data.network.model.request.Login
import kotlinx.coroutines.launch
import timber.log.Timber
/**
 * created by Chirag Variya on 30/09/21
 */
class LoginActivityViewModel : BaseViewModel() {
    private val _login = SingleLiveEvent<CommonResponse<LoginResponse>>()
    val login: LiveData<CommonResponse<LoginResponse>> = _login

    fun login(userLogin: Login) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.login(userLogin)) {
                _login.postValue(it)
            }
        }
    }
}