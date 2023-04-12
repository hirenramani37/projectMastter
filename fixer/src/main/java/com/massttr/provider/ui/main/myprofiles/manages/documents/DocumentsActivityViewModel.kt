package com.massttr.provider.ui.main.myprofiles.manages.documents

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponses
import com.common.data.network.model.Document
import com.common.data.network.model.GetDocumentType
import com.common.data.network.model.request.DeleteDocument
import kotlinx.coroutines.launch

class DocumentsActivityViewModel : BaseViewModel() {
    private val _document = SingleLiveEvent<CommonResponses<Document>>()
    val document: LiveData<CommonResponses<Document>> = _document

    private val _deleteDoc = SingleLiveEvent<CommonResponses<Any>>()
    val deleteDoc: LiveData<CommonResponses<Any>> = _deleteDoc

    private val _getDocumentType = SingleLiveEvent<CommonResponses<GetDocumentType>>()
    val getDocumentType: LiveData<CommonResponses<GetDocumentType>> = _getDocumentType

    fun getDocument() {
        displayLoader()
        viewModelScope.launch {
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
}