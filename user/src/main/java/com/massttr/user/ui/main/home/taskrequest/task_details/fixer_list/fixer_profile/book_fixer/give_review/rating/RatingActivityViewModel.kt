package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.book_fixer.give_review.rating

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.RatingResponse
import com.common.data.network.model.request.Rating
import kotlinx.coroutines.launch

class RatingActivityViewModel : BaseViewModel() {
    private val _rating = SingleLiveEvent<CommonResponse<RatingResponse>>()
    val rating: LiveData<CommonResponse<RatingResponse>> = _rating

    fun ratingUser(rating: Rating) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.rating(rating)) {
                _rating.postValue(it)
            }
        }
    }
}