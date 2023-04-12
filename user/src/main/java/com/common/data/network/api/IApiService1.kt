package com.common.data.network.api

import com.common.data.network.api.IBaseService.Companion.getOkHttpClient
import com.common.data.network.model.*
import com.common.data.network.model.request.*

import com.google.gson.GsonBuilder
import com.massttr.user.BuildConfig
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface IApiService1 : IBaseService {

    //Change Language
    @POST("change-lang")
    suspend fun language(@Body body: Language): Response<CommonResponses<LanguageResponse>>

    //Login
    @POST("user/check-phone")
    suspend fun login(@Body body: Login): Response<CommonResponse<LoginResponse>>

    //Register
    @POST("user/check-phone")
    suspend fun register(@Body body: Registration): Response<CommonResponse<RegistrationResponse>>

    //Verify OTP
    @POST("verify-otp")
    suspend fun otpVerification(@Body body: OTPVerification): Response<CommonResponse<UserInfo>>

    //Resend OTP
    @GET("resend-otp")
    suspend fun otpResend(): Response<OTPResendResponse>

    //Create Profile
    @POST("register")
    suspend fun createProfile(@Body body: CreateProfile): Response<CommonResponse<UserInfo>>

    //Get Profile
    @GET("user/profile")
    suspend fun editProfile(): Response<EditProfileResponse>

    //Update Profile
    @POST("user/profile/update")
    suspend fun updateProfile(@Body body: UpdateProfile): Response<CommonResponse<UserInfo>>

    //FAQ List
    @GET("user/faq/1")
    suspend fun faq(): Response<FaqResponse>

    //Privacy Policy
    @GET("user/pages")
    suspend fun privacyPolicy(): Response<PrivacyPolicyResponse>

    //Contact Us
    @POST("user/contact-us")
    suspend fun contactUs(@Body body: ContactUs): Response<ContactUsResponse>

    //Select Reason List
    @POST("user/reasons")
    suspend fun selectReason(@Body body: SelectReason): Response<SelectReasonResponse>

    //Logout
    @GET("logout")
    suspend fun logout(): Response<LogoutResponse>

    //Delete Account
    @DELETE("delete-account")
    suspend fun delete(): Response<DeleteResponse>

    //Order History List
    @GET("job-history")
    suspend fun orderHistory(
        @Query("env") env: String = "",
        @Query("page", encoded = true) page_number: Int
    ): Response<CommonResponse<OrderHistoryResponse>>

    //Notification List
    @GET("notification-list")
    suspend fun notification(): Response<CommonResponses<NotificationResponse>>

    //Category List
    @GET("category/all")
    suspend fun category(): Response<CommonResponses<CategoryResponse>>

    //Fixer List || Filter
    @POST("job/nearby-fixers")
    suspend fun nearByFixers(@Body body: NearBrFixer): Response<CommonResponse<NearBy>>

    @POST("job/nearby-fixers")
    suspend fun nearByFixersPopup(@Body body: NearBrFixerPopUp): Response<CommonResponse<NearBy>>

    //Fixer Details/Profile
    @POST("fixer/detail")
    suspend fun fixerProfile(@Body body: FixerProfile): Response<CommonResponse<FixerDetailsResponse>>

    //Fixer Details/Profile
    @POST("job/change-status")
    suspend fun bookFixer(@Body body: BookFixer): Response<CommonResponses<BookFixerResponse>>

    @POST("user/job-detail")
    suspend fun jobDetails(@Body body: JobDetails): Response<CommonResponse<JobDetailsResponse>>

    //User Rating
    @POST("rate")
    suspend fun userRating(@Body body: Rating): Response<CommonResponse<RatingResponse>>

    //Change Price
    @POST("job/change-price")
    suspend fun changePrice(@Body body: ChangePrice): Response<CommonResponses<Any>>

    //Book Job
    @POST("user/book-job")
    suspend fun bookJob(@Body body: BookJob): Response<CommonResponse<BookJobResponse>>

    //Home Page Map Get All Fixer
    @POST("home-page")
    suspend fun homePageMap(@Body body: HomeMapGetFixer): Response<CommonResponses<HomeMapGetFixerResponse>>

    //Job/Change Status
    @POST("job/change-status")
    suspend fun jobChangeStatus(@Body body: JobChangeStatus): Response<CommonResponse<Any>>

    //Job Change Status in Complete Task
    @POST("job/change-status")
    suspend fun jobCompleteTaskStatus(@Body body: CompleteTaskStatus): Response<CommonResponse<Any>>

    //Job Change Status in Complete Task
    @GET("setting")
    suspend fun settings(): Response<CommonResponse<SettingsResponse>>

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
