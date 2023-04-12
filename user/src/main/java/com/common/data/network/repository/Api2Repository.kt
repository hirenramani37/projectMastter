package com.common.data.network.repository

import com.common.data.network.api.IApiService2
import okhttp3.MultipartBody

class Api2Repository(
    private val apiService: IApiService2,
) : BaseRepository() {

    /*suspend fun updateProfile(multipartBody: MultipartBody) =
        callApi { apiService.updateProfile(multipartBody) }*/

    suspend fun bookJob(multipartBody: MultipartBody) =
        callApi { apiService.bookJob(multipartBody) }

    suspend fun uploadJobImage(multipartBody: MultipartBody) =
        callApi { apiService.uploadJobImage(multipartBody) }

    suspend fun uploadJobImageMultiple(multipartBody: MultipartBody) =
        callApi { apiService.uploadJobImageMultiple(multipartBody) }

    suspend fun uploadChatMedia(multipartBody: MultipartBody) =
        callApi { apiService.uploadChatMedia(multipartBody) }

    companion object {
        @Volatile
        private var instance: Api2Repository? = null

        fun getInstance(): Api2Repository {
            return instance ?: synchronized(this) {
                instance ?: Api2Repository(IApiService2.getService(false))
                    .also {
                        instance = it
                    }
            }
        }
    }
}