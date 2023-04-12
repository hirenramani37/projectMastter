package com.massttr.provider.ui.main.myprofiles.manages.manageServices

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.Categories
import com.common.data.network.model.ProfileUpdate
import com.common.data.network.model.Subcategory
import com.common.data.network.model.UserInfo
import com.common.data.network.model.request.Category
import com.common.data.network.model.request.UpdateCategory
import com.common.data.network.model.request.UserProfileUpdate
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ManageServiceActivityViewModel : BaseViewModel() {
    private val _service = SingleLiveEvent<List<Categories>>()
    val service: LiveData<List<Categories>> = _service

    private val _subCategory = SingleLiveEvent<List<Subcategory>>()
    val subCategory : LiveData<List<Subcategory>> = _subCategory

    private val _upDateCategory = SingleLiveEvent<Any>()
     val upDateCategories : LiveData<Any> = _upDateCategory

    private val _getProfile = SingleLiveEvent<UserInfo>()
    val getProfile: LiveData<UserInfo> = _getProfile

    fun getService() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.category()) {
                _service.postValue(it.data)
            }
        }
    }

    fun getSubCategory(categoryId: Category){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.subCategory(categoryId)){
                _subCategory.postValue(it.data)
            }
        }
    }

    fun upDateCategory(multipartBody: MultipartBody){
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.upDateCategory(multipartBody)){
                _upDateCategory.postValue(it.data)
            }
        }

    }

    fun getProfile() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.getProfile()) {
                _getProfile.postValue(it.data)
            }
        }
    }
}