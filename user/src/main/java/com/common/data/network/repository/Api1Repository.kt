package com.common.data.network.repository

import com.common.data.network.api.IApiService1
import com.common.data.network.model.request.*

class Api1Repository(private val apiService: IApiService1) : BaseRepository() {


    //login
    suspend fun login(userLogin: Login) = callApi { apiService.login(userLogin) }

    //language
    suspend fun language(lang: Language) = callApi { apiService.language(lang) }

    //register
    suspend fun register(register: Registration) = callApi { apiService.register(register) }

    //OTP Verification
    suspend fun otpVerification(otpVeri: OTPVerification) =
        callApi { apiService.otpVerification(otpVeri) }

    //OTP Resend
    suspend fun otpResend() = callApi { apiService.otpResend() }

    //Create Profile
    suspend fun createProfile(crtProfile: CreateProfile) =
        callApi { apiService.createProfile(crtProfile) }

    //Edit Profile
    suspend fun editProfile() = callApi { apiService.editProfile() }

    //Faq List
    suspend fun faq() = callApi { apiService.faq() }

    //Privacy Policy
    suspend fun privacyPolicy() = callApi { apiService.privacyPolicy() }

    //Contact Us
    suspend fun contactUs(contact: ContactUs) = callApi { apiService.contactUs(contact) }

    //Select Reason List
    suspend fun selectReason(reason: SelectReason) = callApi { apiService.selectReason(reason) }

    //Logout
    suspend fun logout() = callApi { apiService.logout() }

    //Delete Account
    suspend fun delete() = callApi { apiService.delete() }

    //Update Profile
    suspend fun updateProfile(update: UpdateProfile) = callApi { apiService.updateProfile(update) }

    //Order History List
    suspend fun orderHistory(currentPage: Int) = callApi { apiService.orderHistory("test",currentPage) }

    //Notification List
    suspend fun notification() = callApi { apiService.notification() }

    //Category List
    suspend fun category() = callApi { apiService.category() }

    // NearByFixer List
    suspend fun nearByFixers(nearByFixer: NearBrFixer) =
        callApi { apiService.nearByFixers(nearByFixer) }

    //NearByFixer PopUp
    suspend fun nearByFixersPopUp(nearByFixer: NearBrFixerPopUp) =
        callApi { apiService.nearByFixersPopup(nearByFixer) }

    //Fixer Profile/Details
    suspend fun fixerProfile(fixerProfile: FixerProfile) =
        callApi { apiService.fixerProfile(fixerProfile) }

    //Book Fixer
    suspend fun bookFixer(fixer: BookFixer) =
        callApi { apiService.bookFixer(fixer) }

    //Job Details
    suspend fun jobDetails(jobDetails: JobDetails) =
        callApi { apiService.jobDetails(jobDetails) }

    //Rating
    suspend fun rating(rating: Rating) =
        callApi { apiService.userRating(rating) }

    //Change Price
    suspend fun changePrice(change: ChangePrice) =
        callApi { apiService.changePrice(change) }

    //Book Job
    suspend fun bookJob(book: BookJob) =
        callApi { apiService.bookJob(book) }

    //Home Map Get Fixer
    suspend fun mapGetFixer(map: HomeMapGetFixer) =
        callApi { apiService.homePageMap(map) }

    //Job Change Status
    suspend fun jobChangeStatus(jobChange: JobChangeStatus) =
        callApi { apiService.jobChangeStatus(jobChange) }

    //Job Change Status in Complete Task
    suspend fun jobStatusCompleteTask(jobChange: CompleteTaskStatus) =
        callApi { apiService.jobCompleteTaskStatus(jobChange) }

    //Settings
    suspend fun settings() = callApi { apiService.settings() }

    companion object {
        @Volatile
        private var instance: Api1Repository? = null

        fun getInstance(): Api1Repository {
            return instance ?: synchronized(this) {
                instance ?: Api1Repository(IApiService1.getService(true))
                    .also {
                        instance = it
                    }
            }
        }
    }
}
