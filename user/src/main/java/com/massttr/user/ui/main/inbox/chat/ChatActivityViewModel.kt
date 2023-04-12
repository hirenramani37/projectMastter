package com.massttr.user.ui.main.inbox.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.ChatMedia
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ChatActivityViewModel : BaseViewModel() {
    private val _uploadChatMedia = SingleLiveEvent<ChatMedia>()
    val uploadChatMedia: LiveData<ChatMedia> = _uploadChatMedia

    fun uploadChatMedia(multipartBody: MultipartBody) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.uploadChatMedia(multipartBody)) {
                _uploadChatMedia.postValue(it.data)
            }
        }
    }
}
