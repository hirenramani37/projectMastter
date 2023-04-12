package com.massttr.user.ui.main.home.taskrequest.subCategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CategoryResponse
import com.common.data.network.model.CommonResponses
import kotlinx.coroutines.launch

class SubCategoryActivityViewModel : BaseViewModel() {
    private val _category = SingleLiveEvent<CommonResponses<CategoryResponse>>()
    val category: LiveData<CommonResponses<CategoryResponse>> = _category

    fun category() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.category()) {
                _category.postValue(it)
            }
        }
    }
}