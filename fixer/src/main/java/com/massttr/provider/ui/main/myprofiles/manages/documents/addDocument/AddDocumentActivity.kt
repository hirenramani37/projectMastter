package com.massttr.provider.ui.main.myprofiles.manages.documents.addDocument

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import coil.load
import com.common.base.BaseActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityAddDocumentBinding
import com.massttr.provider.ui.language.login.register.verify.fixerRegistration.FixerRegisterViewModel
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.anko.toast
import permissions.dispatcher.*
import java.io.File

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
@RuntimePermissions
class AddDocumentActivity :
    BaseActivity<ActivityAddDocumentBinding>(R.layout.activity_add_document), View.OnClickListener {

    private var docType: Int = 0
    private var commonImagePath: String? = ""
    private val viewModel: FixerRegisterViewModel by viewModels()
    private var resumeDocType: Int = 0

    companion object {
        const val REQ_DOCUMENTS = 0X101
        const val DOCUMENT_TYPE = "document_type"
        const val PICKER_CODE = 77
        const val DOCUMENT_CODE = 101
        const val REQUEST_CODE_SELECT_DOCUMENT = 557
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setObserver()
        clickListener()
    }

    private fun setObserver() {
        viewModel.run {
            apiErrors.observe(this@AddDocumentActivity) { handleError(it) }
            appLoader.observe(this@AddDocumentActivity) { updateLoaderUI(it) }
            uploadDocument.observe(this@AddDocumentActivity) {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun initView() {
        binding.run {
            if (intent.hasExtra(DOCUMENT_TYPE)) {
                docType = intent.getIntExtra(DOCUMENT_TYPE, 0)
                when (docType) {
                    DocType.DOC_DOCUMENT.type -> toolbar.tvTitle.text =
                        getString(R.string.national_id_front)
                    DocType.DOC_MEDICAL.type -> toolbar.tvTitle.text =
                        getString(R.string.national_id_back)
                    DocType.DOC_EDUCATION.type -> toolbar.tvTitle.text =
                        getString(R.string.residence_card)
                    NewDocType.DOC_NATIONAL_ID_FRONT.type -> toolbar.tvTitle.text =
                        getString(R.string.national_id_front)
                    NewDocType.DOC_NATIONAL_ID_BACK.type -> toolbar.tvTitle.text =
                        getString(R.string.national_id_back)
                    NewDocType.DOC_RESIDENCE_CARD.type -> toolbar.tvTitle.text =
                        getString(R.string.residence_card)
                    NewDocType.DOC_RESUME.type -> toolbar.tvTitle.text =
                        getString(R.string.resume)
                }
            }
        }
    }

    private fun validation() {
        when {
            commonImagePath.isNullOrEmpty() -> toast("upload Document")
            else -> uploadDocument()
        }
    }

    private fun uploadBusinessDoc(filePath: String) {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        val file = File(filePath)
        builder.addFormDataPart(
            PRM_DOCUMENT,
            file.name.replace("-", "_"),
            file.asRequestBody("*/*".toMediaTypeOrNull())
        )
        builder.addFormDataPart(PRM_TYPE, docType.toString())
        viewModel.uploadDocument(builder.build())
    }

    private fun uploadDocument() {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        val file = File(commonImagePath.orEmpty())
        builder.addFormDataPart(
            PRM_DOCUMENT,
            file.name.replace("-", "_"),
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )
        builder.addFormDataPart(PRM_TYPE, docType.toString())
        viewModel.uploadDocument(builder.build())

    }

    private fun clickListener() {
        binding.run {
            btnSubmit.setOnClickListener(this@AddDocumentActivity)
            toolbar.imgBack.setOnClickListener(this@AddDocumentActivity)
            ivMain.setOnClickListener(this@AddDocumentActivity)
            rlAdd.setOnClickListener(this@AddDocumentActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSubmit -> validation()
            R.id.imgBack -> onBackPressed()
            R.id.ivMain ->
                ImagePicker
                    .with(this)
                    .compress(1024)
                    .start(PICKER_CODE)
        }
    }

    @NeedsPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onAllowStoragePermission() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "application/pdf"
        startActivityForResult(intent, REQUEST_CODE_SELECT_DOCUMENT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnShowRationale(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onRationaleStoragePermission(request: PermissionRequest) {
        showAskingPermissionDialog("Need Storage Permission to select Document",
            negativeClick = {
                request.cancel()
            }, positiveClick = {
                request.proceed()
            })
    }

    @OnPermissionDenied(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onDeniedStoragePermission() {
    }

    @OnNeverAskAgain(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onNeverAskStoragePermission() {
        showDeniedPermissionDialog(
            "Need Storage Permission to select Document",
            negativeClick = {
                //request.cancel()
            }, positiveClick = {
                // request.proceed()
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICKER_CODE -> {
                    val filePath = PathUtil.getPath(this, data?.data)
                    val file = File(filePath)
                    commonImagePath = file.absolutePath
                    binding.ivMain.load(file.absoluteFile) {
//                        placeholder(R.drawable.user_placeholder)
//                        fallback(R.drawable.user_placeholder)
                    }
                    binding.tvAdd.gone()
                }
                REQUEST_CODE_SELECT_DOCUMENT -> {
                    val fileUri = data?.data!!
                    val fileUtil = FileUtils(this)
                    uploadBusinessDoc(fileUtil.getPath(fileUri))
                }
                else -> toast("Task Cancelled")
            }
        }
        if (resultCode == ImagePicker.RESULT_ERROR)
            toast(ImagePicker.getError(data))
    }
}
