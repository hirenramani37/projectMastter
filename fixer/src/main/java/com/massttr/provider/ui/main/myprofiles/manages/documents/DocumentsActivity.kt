package com.massttr.provider.ui.main.myprofiles.manages.documents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.common.base.BaseActivity
import com.common.data.network.model.GetDocumentType
import com.common.data.network.model.request.DeleteDocument
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityDocumentsBinding
import com.massttr.provider.ui.language.login.register.verify.fixerRegistration.FixerRegistrationActivity
import com.massttr.provider.ui.main.myprofiles.manages.documents.addDocument.AddDocumentActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivityForResult
import timber.log.Timber

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class DocumentsActivity : BaseActivity<ActivityDocumentsBinding>(R.layout.activity_documents),
    View.OnClickListener {

    private val viewModel: DocumentsActivityViewModel by viewModels()
    private val itemList: MutableList<GetDocumentType> = ArrayList()
    //private lateinit var documentAdapter: DocumentListAdapter


   private  var nationalIdFrontAdapter: NationalIdFrontAdapter? = null
//   private lateinit var nationalIdBack: NationalIdBackAdapter
//   private lateinit var residence: ResidenceAdapter
//   private lateinit var resumes: ResumesAdapter

   companion object{
        const val NOTIFICATION = "NOTIFICATION"
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            appLoader.observe(this@DocumentsActivity) { updateLoaderUI(it) }
            apiErrors.observe(this@DocumentsActivity) { handleError(it) }
             deleteDoc.observe(this@DocumentsActivity) {
                 viewModel.getDocument()
                 viewModel.getDocumentType()
             }
            getDocumentType.observe(this@DocumentsActivity) {
                nationalIdFrontAdapter?.addAll(it.data)
                viewModel.getDocument()
            }
            document.observe(this@DocumentsActivity) {
                Timber.d("data: ${it.data}")

                nationalIdFrontAdapter?.displayList?.forEachIndexed { index, document->
                    document.documents = ArrayList(it.data)
                    nationalIdFrontAdapter?.notifyItemChanged(index)
                }

            }
        }
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@DocumentsActivity)
            btnSubmit.setOnClickListener(this@DocumentsActivity)
        }

        nationalIdFrontAdapter?.setItemClickListener { view, i, getDocumentType ->
            when (view.id) {
                R.id.btnAddFront -> {
                    startActivityForResult<AddDocumentActivity>(AddDocumentActivity.REQ_DOCUMENTS, AddDocumentActivity.DOCUMENT_TYPE to getDocumentType.id)
                }
            }
        }
    }

    private fun init() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.documents)
            nationalIdFrontAdapter = NationalIdFrontAdapter(this@DocumentsActivity){ adapter, item, index ->
              deleteDocumentItem(item.id)
            }
            rvFrontDoc.adapter = nationalIdFrontAdapter
            viewModel.getDocumentType()
            viewModel.getDocument()
        }
        if(intent.hasExtra(NOTIFICATION)){
            if(intent.getIntExtra(NOTIFICATION,0) == 2){
               binding.btnView.isVisible = false
            }
        }
    }

    private fun deleteDocumentItem(id: Int) {
        viewModel.deleteDocument(DeleteDocument(id))
    }

    override fun onClick(v: View?) {
        when (v?.id) {

//            R.id.btnResidence->{
//                startActivityForResult<AddDocumentActivity>(
//                    AddDocumentActivity.DOCUMENT_CODE,
//                    AddDocumentActivity.DOCUMENT_TYPE to NewDocType.DOC_RESIDENCE_CARD.type
//                )
//            }
//            R.id.btnAddFront->{
//                startActivityForResult<AddDocumentActivity>(
//                    AddDocumentActivity.DOCUMENT_CODE,
//                    AddDocumentActivity.DOCUMENT_TYPE to NewDocType.DOC_NATIONAL_ID_FRONT.type
//                )
//            }
//            R.id.btnAddBack->{
//                startActivityForResult<AddDocumentActivity>(
//                    AddDocumentActivity.DOCUMENT_CODE,
//                    AddDocumentActivity.DOCUMENT_TYPE to NewDocType.DOC_NATIONAL_ID_BACK.type
//                )
//            }
//            R.id.btnResume->{
//                startActivityForResult<AddDocumentActivity>(
//                    AddDocumentActivity.DOCUMENT_CODE,
//                    AddDocumentActivity.DOCUMENT_TYPE to NewDocType.DOC_RESUME.type
//                )
//            }

            R.id.btnSubmit -> onBackPressed()
            R.id.imgBack -> onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FixerRegistrationActivity.REQ_DOCUMENTS ->{
                    viewModel.getDocument()
                    viewModel.getDocumentType()
                }
            }
        }
    }
}