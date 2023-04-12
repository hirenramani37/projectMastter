package com.common.data.network.api

import com.common.data.network.model.*
import com.common.data.network.model.request.CreateProfile
import com.google.gson.GsonBuilder
import com.massttr.user.BuildConfig
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface IApiService2 : IBaseService {

    //Profile Update
    @POST("user/profile/update")
    suspend fun updateProfile(@Body multipartBody: MultipartBody): Response<CommonResponse<UserInfo>>

    //Book Job/Task Request
    @POST("user/book-job")
    suspend fun bookJob(@Body multipartBody: MultipartBody): Response<BookJobResponse>

    //Book Job Upload Image
    @POST("images-upload")
    suspend fun uploadJobImage(@Body multipartBody: MultipartBody): Response<UploadJobImageResponse>

    @POST("images-upload")
    suspend fun uploadJobImageMultiple(@Body multipartBody: MultipartBody): Response<UploadJobImageResponse>

    //chat media
    @POST("user/chat-media-upload")
    suspend fun uploadChatMedia(@Body multipartBody: MultipartBody): Response<CommonResponse<ChatMedia>>

    companion object {
        fun getService(needEncrypt: Boolean): IApiService2 {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BaseUrl)
                .client(IBaseService.getOkHttpClient(needEncrypt))
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(IApiService2::class.java)
        }
    }
}