package com.common.data.network.repository

import com.common.data.network.api.IApiService2
import okhttp3.MultipartBody


class Api2Repository(
    private val apiService: IApiService2,
) : BaseRepository() {

    suspend fun uploadDocument(multipartBody: MultipartBody) =
        callApi { apiService.uploadDocument(multipartBody) }

    //Fixer Registration
    suspend fun registration(multipartBody: MultipartBody) =
        callApi { apiService.registration(multipartBody) }

    //Upload Image Common API
    suspend fun uploadJobImage(multipartBody: MultipartBody) =
        callApi { apiService.uploadJobImage(multipartBody) }

    suspend fun uploadImage(multipartBody: MultipartBody) = callApi { apiService.uploadImage(multipartBody) }

    // updateCategory
    suspend fun upDateCategory(multipartBody: MultipartBody) = callApi { apiService.upDateCategory(multipartBody) }

    suspend fun uploadChatMedia(multipartBody: MultipartBody) = callApi { apiService.uploadChatMedia(multipartBody) }

    companion object {
        @Volatile
        private var instance: Api2Repository? = null

        fun getInstance(): Api2Repository {
            return instance ?: synchronized(this) {
                instance ?: Api2Repository(IApiService2.getService(false)).also { instance = it }
            }
        }
    }
}