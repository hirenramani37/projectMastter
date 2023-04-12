package com.massttr.provider.ui.language.login.register.verify.fixerRegistration

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.*
import com.common.data.network.model.request.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class FixerRegisterViewModel : BaseViewModel() {
    private val _register = SingleLiveEvent<UserInfo>()
    val register: LiveData<UserInfo> = _register

    private val _state = SingleLiveEvent<List<States>>()
    val state: LiveData<List<States>> = _state

    private val _city = SingleLiveEvent<List<Cities>>()
    val city: LiveData<List<Cities>> = _city

    private val _document = SingleLiveEvent<CommonResponses<Document>>()
    val document: LiveData<CommonResponses<Document>> = _document

    private val _uploadDocument = SingleLiveEvent<UploadDocumentResponse>()
    val uploadDocument: LiveData<UploadDocumentResponse> = _uploadDocument


    private val _service = SingleLiveEvent<List<Categories>>()
    val service: LiveData<List<Categories>> = _service

    private val _subCategory = SingleLiveEvent<List<Subcategory>>()
    val subCategory: LiveData<List<Subcategory>> = _subCategory

    private val _deleteDoc = SingleLiveEvent<CommonResponses<Any>>()
    val deleteDoc: LiveData<CommonResponses<Any>> = _deleteDoc

    private val _getDocumentType = SingleLiveEvent<CommonResponses<GetDocumentType>>()
    val getDocumentType: LiveData<CommonResponses<GetDocumentType>> = _getDocumentType
//
    private val _uploadProfileImage = SingleLiveEvent<CommonUploadImageResponse>()
    val uploadProfileImage: LiveData<CommonUploadImageResponse> = _uploadProfileImage

    private val _fixerRegister = SingleLiveEvent<CommonResponse<UserInfo>>()
    val fixerRegister: LiveData<CommonResponse<UserInfo>> = _fixerRegister

    fun getSubCategory(categoryId: Category) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.subCategory(categoryId)) {
                _subCategory.postValue(it.data)
            }
        }
    }

    fun getService() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.category()) {
                _service.postValue(it.data)
            }
        }
    }

    fun uploadDocument(multipartBody: MultipartBody) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.uploadDocument(multipartBody)) {
                _uploadDocument.postValue(it.data)
            }
        }
    }

    fun registration(multipartBody: MultipartBody) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.registration(multipartBody)) {
                _register.postValue(it.data)
            }
        }
    }

    fun city(city: City) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.city(city)) {
                _city.postValue(it.data)
            }
        }
    }

    fun state() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.state()) {
                _state.postValue(it.data)

            }
        }
    }

    fun getDocument() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.getDocument()) {
                _document.postValue(it)
            }
        }
    }

    fun deleteDocument(delete: DeleteDocument) {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.deleteDocument(delete)) {
                _deleteDoc.postValue(it)
            }
        }
    }

    fun getDocumentType() {
        displayLoader()
        viewModelScope.launch {
            processDataEvent(api1Repository.getDocumentType()) {
                _getDocumentType.postValue(it)
            }
        }
    }

    fun uploadJobImage(multipartBody: MultipartBody) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api2Repository.uploadJobImage(multipartBody)) {
                _uploadProfileImage.postValue(it)
            }
        }
    }

    fun fixerRegister(fixer: FixerRegistration) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.fixerRegister(fixer)) {
                _fixerRegister.postValue(it)
            }
        }
    }
}
