package com.common.data.network.api

import com.common.data.network.api.IBaseService.Companion.getOkHttpClient
import com.common.data.network.model.*
import com.common.data.network.model.request.*
import com.common.data.network.model.request.JobImage
import com.google.gson.GsonBuilder
import com.massttr.provider.BuildConfig
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface IApiService1 : IBaseService {

    //Change Language
    @POST("change-lang")
    suspend fun language(@Body body: Language): Response<LanguageResponse>

    //Delete Profile
    @DELETE("delete-account")
    suspend fun deleteAccount(): Response<DeleteResponse>

    //OTP Verification
    @POST("verify-otp")
    suspend fun otpVerification(@Body body: OTPVerification): Response<CommonResponse<UserInfo>>

    //OTP Resend
    @GET("resend-otp")
    suspend fun resendOTP(): Response<OTPResendResponse>

    //Login
    @POST("check-phone")
    suspend fun login(@Body body: Login): Response<CommonResponse<RegisterResponse>>

    //  Register
    @POST("check-phone")
    suspend fun registration(@Body body: CreateRegistration): Response<CommonResponse<RegisterResponse>>

    @GET("profile")
    suspend fun getProfile(): Response<CommonResponse<UserInfo>>

    @POST("profile/update")
    suspend fun updateProfile(@Body body: UserProfileUpdate): Response<CommonResponse<ProfileUpdate>>

    //Notification List
    @GET("notification-list")
    suspend fun notification(): Response<CommonResponses<NotificationResponse>>

    @GET("state")
    suspend fun state(): Response<CommonResponses<States>>

    @POST("cities")
    suspend fun city(@Body body: City): Response<CommonResponses<Cities>>

    //Logout
    @GET("logout")
    suspend fun logout(): Response<LogoutResponse>

    //View Task Accept
    @POST("job/details")
    suspend fun viewTask(@Body body: ViewTaskAccept): Response<CommonResponse<ViewTaskResponse>>

    //View Task Accept
    @POST("job/interested")
    suspend fun interested(@Body body: JobInterested): Response<CommonResponse<Any>>

    //nearby-jobs
    @POST("nearby-jobs")
    suspend fun nearbyJobs(@Body body: AvailableTask):Response<CommonResponses<NearByJobsResponse>>

    //Available Task
    @POST("available-jobs")
    suspend fun availableTask(@Body body: AvailableTask, @Query("page") page_number: Int): Response<CommonResponse<AvailableTaskResponse>>

    //Fixer Location
    @POST("location")
    suspend fun fixerLocation(@Body body: FixerLocation): Response<CommonResponse<FixerLocationResponse>>

    //Get Document List
    @GET("get-documents?type=2")
    suspend fun getDocument(): Response<CommonResponses<Document>>

    //Delete Document
    @POST("delete-document")
    suspend fun deleteDocument(@Body body: DeleteDocument): Response<CommonResponses<Any>>

    //Delete Photo job
    @POST("job/delete-image")
    suspend fun bookJobDeleteImage(@Body body: DeleteJobPhoto): Response<CommonResponses<Any>>

    //FAQ
    @GET("faq/2")
    suspend fun faq(): Response<CommonResponses<FaqResponse>>

    //Help Center
    @GET("pages")
    suspend fun helpCenter(): Response<CommonResponses<PrivacyPolicyResponse>>

    //Contact Us
    @POST("contact-us")
    suspend fun contactUs(@Body body: ContactUs): Response<CommonResponses<ContactUsResponse>>

    //Reason List
    @POST("reasons")
    suspend fun selectReason(@Body body: SelectReason): Response<CommonResponses<SelectReasonResponse>>

    //Online-Offline Status
    @POST("online-offline-status")
    suspend fun statusCheck(@Body body: StatusCheck): Response<CommonResponse<StatusCheckResponse>>

    //Completed job
    @POST("job/completed")
    suspend fun completedJob(@Query("page") page_number: Int): Response<CommonResponse<CompleteJob>>

    @POST("job/change-status")
    suspend fun changeJobStatus(@Body body: JobStatus): Response<CommonResponse<Any>>

    @POST("job/change-status")
    suspend fun jobChangeStatus(@Body body: JobChangeStatus): Response<CommonResponse<Any>>

    // getTaskRequestImage
    @POST("job/get-images")
    suspend fun getTaskRequestImage(@Body body: JobImage): Response<CommonResponses<com.common.data.network.model.JobImage>>

    //Fix List Upcoming
    @GET("job/my-fix")
    suspend fun fixList(@Query("page") page_number: Int): Response<CommonResponse<MyFix>>

    //Earnings
    @POST("earning")
    suspend fun fixerEarning(@Body body: FixerEarning): Response<CommonResponse<EarningResponse>>

    @GET("category/all")
    suspend fun getCategory(): Response<CommonResponses<Categories>>

    // subCategory
    @POST("sub-categories")
    suspend fun subCategory(@Body body: Category): Response<CommonResponses<Subcategory>>

    // UpdateCategory
    @POST("category/update")
    suspend fun upDateCategory(@Body multipartBody: MultipartBody): Response<CommonResponse<Any>>

    @GET("document-type")
    suspend fun getDocumentType(): Response<CommonResponses<GetDocumentType>>

    @GET("https://massttr.com/api/v1/setting")
    suspend fun getSettings(): Response<CommonResponse<GetSettings>>

    @POST("register")
    suspend fun fixerRegister(@Body body: FixerRegistration): Response<CommonResponse<UserInfo>>

    companion object {
        fun getService(needEncrypt: Boolean): IApiService1 {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BaseUrl)
                .client(getOkHttpClient(needEncrypt))
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .addConverterFactory(ScalarsConverterFactory.create())
                .build().create(IApiService1::class.java)
        }
    }
}
