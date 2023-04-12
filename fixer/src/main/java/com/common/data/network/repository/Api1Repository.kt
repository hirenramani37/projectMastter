package com.common.data.network.repository

import com.common.data.network.api.IApiService1
import com.common.data.network.model.request.*
import okhttp3.MultipartBody

class Api1Repository(private val apiService: IApiService1) : BaseRepository() {

//    suspend fun login(map: HashMap<String, String>) =
//        callApi { apiService.login(map) }

    //Delete Profile
    suspend fun deleteProfile() = callApi { apiService.deleteAccount() }


    suspend fun getProfile() = callApi { apiService.getProfile() }

    suspend fun updateProfile(userProfileUpdate: UserProfileUpdate) =
        callApi { apiService.updateProfile(userProfileUpdate) }

    //OTP Verification
    suspend fun otpVerification(verification: OTPVerification) =
        callApi { apiService.otpVerification(verification) }

    //OTP Resend
    suspend fun resendOTP() = callApi { apiService.resendOTP() }

    // Register
    suspend fun register(register: CreateRegistration) =
        callApi { apiService.registration(register) }

    //Login
    suspend fun login(login: Login) = callApi { apiService.login(login) }

    //Logout
    suspend fun logout() = callApi { apiService.logout() }

    //Notification
    suspend fun notification() = callApi { apiService.notification() }

    //getCity
    suspend fun city(city: City) = callApi { apiService.city(city) }

    //getState
    suspend fun state() = callApi { apiService.state() }

    //language
    suspend fun language(lang: Language) = callApi { apiService.language(lang) }

    //Available Task
    suspend fun availableTask(availableTask: AvailableTask, currentPage: Int) =
        callApi { apiService.availableTask(availableTask, currentPage) }

    //near jobs
    suspend fun nearJobs(availableTask: AvailableTask) =
        callApi { apiService.nearbyJobs(availableTask) }

    //Fixer Location
    suspend fun fixerLocation(fixerLocation: FixerLocation) =
        callApi { apiService.fixerLocation(fixerLocation) }

    //View Task Accept
    suspend fun viewTask(viewTask: ViewTaskAccept) =
        callApi { apiService.viewTask(viewTask) }

    // interestedjob
    suspend fun interestedJob(jobInterested: JobInterested) =
        callApi { apiService.interested(jobInterested) }

    // getDocument
    suspend fun getDocument() = callApi { apiService.getDocument() }

    // Delete Document
    suspend fun deleteDocument(deleteDoc: DeleteDocument) =
        callApi { apiService.deleteDocument(deleteDoc) }

    // Delete Document
    suspend fun deleteJobImage(deleteJobPhoto: DeleteJobPhoto) =
        callApi { apiService.bookJobDeleteImage(deleteJobPhoto) }

    //FAQ
    suspend fun faq() = callApi { apiService.faq() }

    suspend fun helpCenter() = callApi { apiService.helpCenter() }

    //ContactUs
    suspend fun contactUs(contact: ContactUs) = callApi { apiService.contactUs(contact) }

    //Reason List
    suspend fun selectReason(reason: SelectReason) = callApi { apiService.selectReason(reason) }

    //Status Check
    suspend fun statusCheck(status: StatusCheck) = callApi { apiService.statusCheck(status) }

    //Completed Job
    suspend fun completedJob(currentPage: Int) = callApi { apiService.completedJob(currentPage) }

    // change status
    suspend fun changeJobStatus(jobStatus: JobStatus) =
        callApi { apiService.changeJobStatus(jobStatus) }

    suspend fun changeJobStatus2(jobStatus: JobChangeStatus) =
        callApi { apiService.jobChangeStatus(jobStatus) }

    //get image task
    suspend fun getTaskImage(jobImage: com.common.data.network.model.request.JobImage) =
        callApi { apiService.getTaskRequestImage(jobImage) }

    //FixList Upcoming
    suspend fun fixList(currentPage: Int) = callApi { apiService.fixList(currentPage) }

    //Earning
    suspend fun fixerEarning(earning: FixerEarning) = callApi { apiService.fixerEarning(earning) }

    // get service
    suspend fun category() = callApi { apiService.getCategory() }

    // SubCategory
    suspend fun subCategory(categoryId: Category) = callApi { apiService.subCategory(categoryId) }

    // updateCategory
    suspend fun upDateCategory(multipartBody: MultipartBody) =
        callApi { apiService.upDateCategory(multipartBody) }

    // Get Document List
    suspend fun getDocumentType() = callApi { apiService.getDocumentType() }

    suspend fun getSetting() = callApi { apiService.getSettings() }

    //Fixer Registration
    suspend fun fixerRegister(fixer: FixerRegistration) =
        callApi { apiService.fixerRegister(fixer) }

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
