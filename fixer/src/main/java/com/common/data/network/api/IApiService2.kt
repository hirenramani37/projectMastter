package com.common.data.network.api

import com.common.data.network.model.*
import com.google.gson.GsonBuilder
import com.massttr.provider.BuildConfig
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface IApiService2 : IBaseService {

    @POST("upload-documents")
    suspend fun uploadDocument(@Body multipartBody: MultipartBody): Response<CommonResponse<UploadDocumentResponse>>

    //Fixer Registration
    @POST("register")
    suspend fun registration(@Body multipartBody: MultipartBody): Response<CommonResponse<UserInfo>>

    //uploadImage
    @POST("job/upload-image")
    suspend fun uploadImage(@Body multipartBody: MultipartBody): Response<CommonResponses<JobImage>>

    //Book Job Upload Image
    @POST("https://massttr.com/api/v1/images-upload")
    suspend fun uploadJobImage(@Body multipartBody: MultipartBody): Response<CommonUploadImageResponse>

    @POST("category/update")
    suspend fun upDateCategory(@Body multipartBody: MultipartBody): Response<CommonResponse<Any>>

    //chat media
    @POST("chat-media-upload")
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
