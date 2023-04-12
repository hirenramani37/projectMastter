package com.massttr.provider.ui.language.login.register.verify.fixerRegistration

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.common.base.BaseActivity
import com.common.data.network.model.*
import com.common.data.network.model.request.Category
import com.common.data.network.model.request.DeleteDocument
import com.common.data.network.model.request.FixerRegistration
import com.github.dhaval2404.imagepicker.ImagePicker
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityFixerRegistrationBinding
import com.massttr.provider.ui.language.login.register.verify.fixerRegistration.success.SuccessfullyActivity
import com.massttr.provider.ui.main.myprofiles.manages.documents.*
import com.massttr.provider.ui.main.myprofiles.manages.documents.addDocument.AddDocumentActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class FixerRegistrationActivity :
    BaseActivity<ActivityFixerRegistrationBinding>(R.layout.activity_fixer_registration),
    View.OnClickListener {
    private val viewModel: FixerRegisterViewModel by viewModels()
    private var position: Int = 0

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    private var categoryData = ArrayList<Categories>()
    private lateinit var nationalIdFrontAdapter: NationalIdFrontAdapter

    private var subCategoryData = ArrayList<Subcategory>()
    private var cal = Calendar.getInstance()
    private var selectedGender = 0
    private var recepit = 0
    var fullName: String = ""
    var mobileNo: String = ""
    private var profileImagePath: String? = ""
    private var mapCityName: String? = ""
    private var mapStateName: String? = ""
    private var mapZipCode: String? = ""

    companion object {
        const val FULL_NAME = "FULL_NAME"
        const val MOBILE_NUMBER = "MOBILE_NUMBER"
        const val REQ_DOCUMENTS = 0X101
        const val REQ_PROFILE_IMAGE = 0X102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        selectedTab(position)
        setUpUI()
        setObserver()
        clickListener()
        getBusEvent()
    }

    private fun initView() {
        binding.toolbar.tvTitle.text = getString(R.string.fixer_registration)
        viewModel.state()
        viewModel.getDocumentType()
        viewModel.getDocument()
        viewModel.getService()  // it category

        if (intent.hasExtra(MOBILE_NUMBER)) {
            mobileNo = intent.getStringExtra(MOBILE_NUMBER).toString()
            binding.etMno.isEnabled = false
            binding.etMno.isClickable = false
        }
        if (intent.hasExtra(FULL_NAME)) {
            fullName = intent.getStringExtra(FULL_NAME).toString()
        }
        binding.run {

            etMno.setText(mobileNo)
            etFname.setText(fullName)

            categoryAdapter = CategoryAdapter(this@FixerRegistrationActivity)
            rvCategorys.adapter = categoryAdapter

            subCategoryAdapter = SubCategoryAdapter()
            rvSubCateGorys.adapter = subCategoryAdapter

            nationalIdFrontAdapter =
                NationalIdFrontAdapter(this@FixerRegistrationActivity) { _, item, _ ->
                    deleteDocumentItem(item.id)
                }
            rvFrontDoc.adapter = nationalIdFrontAdapter
        }

        categoryAdapter.setItemClickListener { view, i, categoryData ->
            when (view.id) {
                R.id.ivDeleteCategory -> {
                    categoryAdapter.displayList.removeAt(i)
                    subCategoryList(categoryData.id.toString())
                    categoryAdapter.notifyDataSetChanged()
                }
            }
        }

        subCategoryAdapter.setItemClickListener { _, i, subCategory ->
            subCategoryData.forEachIndexed { index, subCategoryData ->
                if (subCategory.id == subCategoryData.id) {
                    subCategoryData.isSelected = false
                }
            }
            subCategoryAdapter.displayList.removeAt(i)
            subCategoryAdapter.notifyDataSetChanged()
        }

        binding.rvGender.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbMale)
                selectedGender = 0
            else if (checkedId == R.id.rbFemale)
                selectedGender = 1
        }

        binding.cbReceipt.setOnCheckedChangeListener { compoundButton, isCheck ->
            recepit = if (isCheck) {
                1
            } else {
                0
            }
        }
    }

    private fun deleteDocumentItem(id: Int) {
        viewModel.deleteDocument(DeleteDocument(id))
    }

    private fun setObserver() {
        viewModel.run {
            apiErrors.observe(this@FixerRegistrationActivity) { handleError(it) }
            appLoader.observe(this@FixerRegistrationActivity) { updateLoaderUI(it) }
//            register.observe(this@FixerRegistrationActivity) {
//                pref.isLogin = true
////                pref.authToken = it.api_token
//                pref.userInfo = it
//
//                startActivity<SuccessfullyActivity>()
//            }
            fixerRegister.observe(this@FixerRegistrationActivity){
                pref.isLogin = true
                pref.userInfo = it.data
                startActivity<SuccessfullyActivity>()
            }

            document.observe(this@FixerRegistrationActivity) {
                nationalIdFrontAdapter.displayList.forEachIndexed { index, document ->
                    document.documents = ArrayList(it.data)
                    nationalIdFrontAdapter.notifyItemChanged(index)
                }
            }

            subCategory.observe(this@FixerRegistrationActivity) {
                subCategoryData = it as ArrayList<Subcategory>
                subCategoryAdapter.addAll((subCategoryData).filter { it.isSelected })
            }
            service.observe(this@FixerRegistrationActivity) {
                categoryData = it as ArrayList<Categories>
                categoryAdapter.addAll((categoryData).filter { it.isSelected })
            }
            deleteDoc.observe(this@FixerRegistrationActivity) {
                //viewModel.getDocument()
                viewModel.getDocument()
                viewModel.getDocumentType()
                showMessage("Document deleted successfully.")
            }
            getDocumentType.observe(this@FixerRegistrationActivity) {
                nationalIdFrontAdapter.addAll(it.data)
                viewModel.getDocument()
            }
            uploadProfileImage.observe(this@FixerRegistrationActivity) {
                if (it.success) {
                    it.data.forEach { imagePath ->
                        profileImagePath = imagePath
                    }
                }
            }
        }
    }

    private fun subCategoryList(id: String) {
        val category = Category(id)
        viewModel.getSubCategory(category)
    }

    fun onClickDelete(id: Int) {
        viewModel.deleteDocument(DeleteDocument(id))
    }

    private fun hideSoftKeyboard(activity: Activity?) {
        if (null == activity) {
            return
        }
        val inputMethodManager: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus
        if (null != view) {
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken, 0
            )
        }
    }

    private fun clickListener() {
        binding.run {
            tvNext.setOnClickListener(this@FixerRegistrationActivity)
            tvPrevious.setOnClickListener(this@FixerRegistrationActivity)
            toolbar.imgBack.setOnClickListener(this@FixerRegistrationActivity)
            tvDOB.setOnClickListener(this@FixerRegistrationActivity)
            ivService.setOnClickListener(this@FixerRegistrationActivity)
            ivSubCategory.setOnClickListener(this@FixerRegistrationActivity)
            civProfileImage.setOnClickListener(this@FixerRegistrationActivity)
            ivProfile.setOnClickListener(this@FixerRegistrationActivity)
            ivLocation.setOnClickListener(this@FixerRegistrationActivity)
            ivCompanyLocation.setOnClickListener(this@FixerRegistrationActivity)
        }
    }

    private fun setUpUI() {

        nationalIdFrontAdapter.setItemClickListener { view, i, getDocumentType ->
            when (view.id) {
                R.id.btnAddFront -> {
                    startActivityForResult<AddDocumentActivity>(
                        AddDocumentActivity.REQ_DOCUMENTS,
                        AddDocumentActivity.DOCUMENT_TYPE to getDocumentType.id
                    )
                }
            }
        }
    }

    private fun getDocumentsList(
        docList: CommonResponses<Document>,
        documentName: String,
        maxDoc: Int
    ): ArrayList<DocumentData> {
        return arrayListOf(
            DocumentData(
                documentName,
                NewDocType.DOC_NATIONAL_ID_FRONT.type,
                maxDoc,
                documentName,
                (docList.data.filter { it.type == NewDocType.DOC_NATIONAL_ID_FRONT.type } as ArrayList<Document>)),
            DocumentData(
                documentName,
                NewDocType.DOC_NATIONAL_ID_BACK.type,
                maxDoc,
                documentName,
                (docList.data.filter { it.type == NewDocType.DOC_NATIONAL_ID_BACK.type } as ArrayList<Document>)),
            DocumentData(
                documentName,
                NewDocType.DOC_RESIDENCE_CARD.type,
                maxDoc,
                documentName,
                (docList.data.filter { it.type == NewDocType.DOC_RESIDENCE_CARD.type } as ArrayList<Document>)),
            DocumentData(
                documentName,
                NewDocType.DOC_RESUME.type,
                maxDoc,
                documentName,
                (docList.data.filter { it.type == NewDocType.DOC_RESUME.type } as ArrayList<Document>)))
    }

    private fun selectedColor(
        @ColorInt color: Int = ContextCompat.getColor(
            this@FixerRegistrationActivity,
            R.color.colorPrimaryDark
        ),
    ): ColorStateList {
        return ColorStateList.valueOf(color)
    }

    private fun unselectedColor(
        @ColorInt color: Int = ContextCompat.getColor(
            this@FixerRegistrationActivity,
            R.color.tex_grayTwo
        ),
    ): ColorStateList {
        return ColorStateList.valueOf(color)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvNext -> {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                when (position) {
                    0 -> {
                        if (validateFirst())
                            position++
                        selectedTab(position)
                    }
                    1 -> {
                        //if (validateSecond())
                        position++
                        selectedTab(position)
                    }
                    2 -> {
                        viewModel.getDocument()
                        viewModel.getDocumentType()
                        if (documentsVerify()) {
                            position++
                            selectedTab(position)
                        }
                    }
                    3 ->
                        if (validateFourth())
                            registration()
                }
            }
            R.id.tvPrevious -> {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                position--
                selectedTab(position)
            }
            R.id.btnAddDocument -> startActivityForResult<AddDocumentActivity>(
                AddDocumentActivity.DOCUMENT_CODE,
                AddDocumentActivity.DOCUMENT_TYPE to DocType.DOC_DOCUMENT.type
            )
//            R.id.btnAddDocHealth -> startActivityForResult<AddDocumentActivity>(
//                AddDocumentActivity.DOCUMENT_CODE,
//                AddDocumentActivity.DOCUMENT_TYPE to DocType.DOC_MEDICAL.type
//            )
//            R.id.btnAddDocEducation -> startActivityForResult<AddDocumentActivity>(
//                AddDocumentActivity.DOCUMENT_CODE,
//                AddDocumentActivity.DOCUMENT_TYPE to DocType.DOC_EDUCATION.type
//            )
            R.id.imgBack -> onBackPressed()
            R.id.tvDOB -> {
                hideSoftKeyboard(this)
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]

                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, year, monthOfYear, dayOfMonth ->
                        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                        val selectedDate =
                            dateFormat.parse(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString())
                        binding.tvDOB.text = dateFormat.format(selectedDate ?: Date())
                    }, mYear, mMonth, mDay
                )

                datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 10000
                datePickerDialog.show()

            }
            R.id.civProfileImage -> {
                ImagePicker
                    .with(this)
                    .cropSquare()
                    .compress(1024)
                    .start(REQ_PROFILE_IMAGE)
            }
            R.id.ivService -> {
                val intent = Intent(this, FixerServiceActivity::class.java)
                intent.putExtra(FixerServiceActivity.CATEGORY, categoryAdapter.displayList)
                startActivityForResult(intent, 101)
            }
            R.id.ivLocation -> startActivity<LocationActivity>()
            R.id.ivCompanyLocation -> startActivity<LocationActivity>()
            R.id.ivSubCategory -> {
                val categoryId = categoryAdapter.displayList
                    .filter { it.isSelected }
                    .map { it.id }
                    .joinToString(",")
                if (categoryAdapter.displayList.map { it.id }.joinToString().isEmpty()) {
                    toast("Please Select Service")
                } else {
                    val intent = Intent(this, FixerServiceActivity::class.java)
                    intent.putExtra(
                        FixerServiceActivity.SUB_CATEGORY_IDS,
                        subCategoryAdapter.displayList
                    )
                    intent.putExtra("category_id", categoryId)
                    startActivityForResult(intent, 200)
                }
            }

//            R.id.btnResidence->{
//                startActivityForResult<AddDocumentActivity>(
//                    REQ_DOCUMENTS,
//                    AddDocumentActivity.DOCUMENT_TYPE to NewDocType.DOC_RESIDENCE_CARD.type
//                )
//            }
            R.id.btnAddFront -> {
                startActivityForResult<AddDocumentActivity>(
                    REQ_DOCUMENTS,
                    AddDocumentActivity.DOCUMENT_TYPE to NewDocType.DOC_NATIONAL_ID_FRONT.type
                )
            }
//            R.id.btnAddBack->{
//                startActivityForResult<AddDocumentActivity>(
//                    REQ_DOCUMENTS,
//                    AddDocumentActivity.DOCUMENT_TYPE to NewDocType.DOC_NATIONAL_ID_BACK.type
//                )
//            }
//            R.id.btnResume->{
//                startActivityForResult<AddDocumentActivity>(
//                    REQ_DOCUMENTS,
//                    AddDocumentActivity.DOCUMENT_TYPE to NewDocType.DOC_RESUME.type
//                )
//            }
        }
    }

    private fun documentsVerify(): Boolean {

        return when {
            nationalIdFrontAdapter.displayList.isEmpty() -> {
//                Timber.e("CheckSubAdapterListDoc: ${nationalIdFront.displayList}")
                toast(getString(R.string.driving_license_passport))
                false
            }
//            nationalIdBack.displayList.isEmpty() -> {
////                Timber.e("CheckSubAdapterListDoc: ${nationalIdFront.displayList}")
//                toast(getString(R.string.driving_license_passport))
//                false
//            }
//            residence.displayList.isEmpty() -> {
////                Timber.e("CheckSubAdapterListDoc: ${nationalIdFront.displayList}")
//                toast(getString(R.string.driving_license_passport))
//                false
//            }
//            resumes.displayList.isEmpty() -> {
////                Timber.e("CheckSubAdapterListDoc: ${nationalIdFront.displayList}")
//                toast(getString(R.string.driving_license_passport))
//                false
//            }
//            documentsAdapter.list.isEmpty() -> {
//                Timber.e("CheckSubAdapterList: ${documentsAdapter.recordAdapter.list}")
//                toastError("Please Upload Document")
//                false
//            }
//            documentsAdapter.list[1].documentList.isEmpty() -> {
//                Timber.e("CheckSubAdapterListDoc: ${documentsAdapter.recordAdapter.list}")
//                toastError("Please Upload Criminal Record ")
//                false
//            }
//            documentAdapter.displayList[1].documentList.isEmpty() -> {
//                Timber.e("CheckSubAdapterListDoc: ${documentAdapter.recordAdapter.displayList}")
//                toast(getString(R.string.driving_license_passport))
//                false
//            }
            else -> true
        }
    }

    private fun validateFirst(): Boolean {
        binding.run {
            when {
                etFname.isBlank() -> setShakeError(getString(R.string.please_enter_Fname))
                etMno.isBlank() -> setShakeError(getString(R.string.please_enter_number))
                tvDOB.text.toString()
                    .isEmpty() -> setShakeError(getString(R.string.please_choose))
                etAddress.isBlank() -> setShakeError(getString(R.string.please_enter_address))
                profileImagePath?.isEmpty() == true ->
                    setShakeError("Please Upload Profile Image")
                //toast("Please Upload Profile Image")
                else -> return true
            }
            return false
        }
    }

    private fun validateSecond(): Boolean {
        binding.run {
            when {
                etCompanyName.isBlank() -> setShakeError(getString(R.string.please_enter_company_name))
                etCVRNumber.isBlank() -> setShakeError(getString(R.string.please_enter_cvr))
                etCompanyAddress.isBlank() -> setShakeError(getString(R.string.please_enter_add))
                else -> return true
            }
            return false
        }
    }

    private fun registration() {
        binding.run {
//            val builder = MultipartBody.Builder()
//            builder.setType(MultipartBody.FORM)
//
//            val documentFront = File(profileImagePath.orEmpty())
//            builder.addFormDataPart(
//                PRM_PROFILE_PICTURE,
//                documentFront.name.replace("-", "_"),
//                documentFront.asRequestBody("image/*".toMediaTypeOrNull())
//            )
//            builder.addFormDataPart(PRM_DEFAULT_LANG, "en")
//            builder.addFormDataPart(PRM_FULL_NAME, etFname.getTextString())
//            builder.addFormDataPart(PRM_COUNTRY_CODE, "964")
//            builder.addFormDataPart(PRM_PHONE_NUMBER, etMno.getTextString())
//            builder.addFormDataPart("email_id", etEmailId.getTextString())
//            builder.addFormDataPart(PRM_BIRTH_DAY, tvDOB.text.toString())
//            builder.addFormDataPart(PRM_GENDER, selectedGender.toString())
//            builder.addFormDataPart(PRM_ZIP_CODE, etZipCode.getTextString())
//            builder.addFormDataPart(PRM_ADDRESS, etAddress.getTextString())
//            builder.addFormDataPart(PRM_STATE, "1")
//            builder.addFormDataPart(PRM_CITY, "1")
//            builder.addFormDataPart(PRM_ENV, "test")
//            builder.addFormDataPart("city_name", mapCityName.toString())
//            builder.addFormDataPart("state_name", mapStateName.toString())
//            builder.addFormDataPart(PRM_DEVICE_TOKEN, pref.fcmToken.toString())
//            builder.addFormDataPart(PRM_SEND_INVOICE, recepit.toString())
//            builder.addFormDataPart(PRM_COMPANY_EMAIL, etCompanyEmailId.getTextString())
//            builder.addFormDataPart(PRM_COMPANY_VAT_NUMBER, etCVRNumber.getTextString())
//            builder.addFormDataPart(PRM_COMPANY_NAME, etCompanyName.getTextString())
//            builder.addFormDataPart(PRM_COMPANY_ZIP_CODE, etCompanyZipCode.getTextString())
//            builder.addFormDataPart(PRM_COMPANY_ADDRESS, etCompanyAddress.getTextString())
//            builder.addFormDataPart(PRM_SUBCATEGORY_IDS,
//                subCategoryAdapter.displayList.filter { it.isSelected }.map { it.id }
//                    .joinToString(","))
//            builder.addFormDataPart(PRM_COMMENTS, etServices.getTextString())
//            viewModel.registration(builder.build())
            viewModel.fixerRegister(FixerRegistration(
                "en",
                etFname.getTextString(),
                964,
                etMno.getTextString(),
                etEmailId.getTextString(),
                tvDOB.text.toString(),
                selectedGender,
                etZipCode.getTextString(),
                etAddress.getTextString(),
                1,
                1,
                etCompanyName.getTextString(),
                etCVRNumber.getTextString(),
                etCompanyZipCode.getTextString(),
                etCompanyEmailId.getTextString(),
                etCompanyAddress.getTextString(),
                subCategoryAdapter.displayList.filter { it.isSelected }.map { it.id }
                    .joinToString(","),
                etServices.getTextString(),
                pref.fcmToken.toString(),
                recepit,
                profileImagePath.toString(),
                mapCityName.toString(),
                mapStateName.toString()
            ))
        }
    }

    private fun validateFourth(): Boolean {
        binding.run {
            when {
                etServices.isBlank() -> setShakeError(getString(R.string.please_enter_services))
                pref.fcmToken.isNullOrEmpty() -> {
                    AppGlobal.getFcmToken(this@FixerRegistrationActivity) {
                        registration()
                    }
                }
                else -> return true
            }
            return false
        }
    }

    private fun selectedTab(index: Int) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        when (index) {
            0 -> {
                binding.run {
                    tvOne.backgroundTintList = selectedColor()
                    viewOne.backgroundTintList = selectedColor()
                    tvTwo.backgroundTintList = unselectedColor()
                    viewTwo.backgroundTintList = unselectedColor()
                    tvThree.backgroundTintList = unselectedColor()
                    viewThree.backgroundTintList = unselectedColor()
                    tvFour.backgroundTintList = unselectedColor()
                    tvNext.text = getString(R.string.next)
                    clPersonalDetails.visible()
                    llCompany.gone()
                    llDocument.gone()
                    llService.gone()
                    tvNext.visible()
                    tvPrevious.gone()
                }
            }
            1 -> {
                binding.run {
                    tvOne.backgroundTintList = selectedColor()
                    viewOne.backgroundTintList = selectedColor()
                    tvTwo.backgroundTintList = selectedColor()
                    viewTwo.backgroundTintList = unselectedColor()
                    tvThree.backgroundTintList = unselectedColor()
                    viewThree.backgroundTintList = unselectedColor()
                    tvFour.backgroundTintList = unselectedColor()
                    tvNext.text = getString(R.string.next)
                    clPersonalDetails.gone()
                    llCompany.visible()
                    llDocument.gone()
                    llService.gone()
                    tvNext.visible()
                    tvPrevious.visible()
                }
            }
            2 -> {
                binding.run {
                    tvOne.backgroundTintList = selectedColor()
                    viewOne.backgroundTintList = selectedColor()
                    tvTwo.backgroundTintList = selectedColor()
                    viewTwo.backgroundTintList = selectedColor()
                    tvThree.backgroundTintList = selectedColor()
                    viewThree.backgroundTintList = unselectedColor()
                    tvFour.backgroundTintList = unselectedColor()
                    tvNext.text = getString(R.string.next)
                    clPersonalDetails.gone()
                    llCompany.gone()
                    llDocument.visible()
                    llService.gone()
                    tvNext.visible()
                    tvPrevious.visible()
                }
            }
            3 -> {
                binding.run {
                    tvOne.backgroundTintList = selectedColor()
                    viewOne.backgroundTintList = selectedColor()
                    tvTwo.backgroundTintList = selectedColor()
                    viewTwo.backgroundTintList = selectedColor()
                    tvThree.backgroundTintList = selectedColor()
                    viewThree.backgroundTintList = selectedColor()
                    tvFour.backgroundTintList = selectedColor()
                    clPersonalDetails.gone()
                    llCompany.gone()
                    llDocument.gone()
                    llService.visible()
                    tvNext.visible()
                    tvPrevious.visible()
                    tvNext.text = getString(R.string.registration)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateInView() {
        binding.tvDOB.text = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_DOCUMENTS -> {
                    viewModel.getDocument()
                    viewModel.getDocumentType()
                }
                101 -> {
                    if (resultCode == Activity.RESULT_OK) {
                        categoryAdapter.clearAll()

                        val alCategory =
                            data?.getSerializableExtra(PRM_SELECTED_CATEGORY) as ArrayList<Categories>

                        val alCatSelected = alCategory.filter { it.isSelected }.toList()

                        alCatSelected.let { categoryAdapter.addAll(it) }
                        alCategory.forEachIndexed { _, selectdata ->
                            categoryAdapter.displayList.forEachIndexed { index, cdata ->
                                if (selectdata.id == cdata.id) {
                                    subCategoryLis(categoryAdapter.displayList.map { it.id }
                                        .joinToString())
                                }
                            }
                        }
                    }
                }
                200 -> {
                    if (resultCode == Activity.RESULT_OK) {
                        subCategoryAdapter.clearAll()

                        val alSubCategory =
                            data?.getSerializableExtra(PRM_SELECTED_SUBCATEGORY) as ArrayList<Subcategory>
                        val alSubCatSelected = alSubCategory.filter { it.isSelected }.toList()

                        alSubCatSelected.let { subCategoryAdapter.addAll(it ?: emptyList()) }
                    }
                }
                REQ_PROFILE_IMAGE -> {
//                    val fileUri = data?.data
//                    val filePath = PathUtil.getPath(this, data?.data)
//                    val file = File(filePath)
//                    profileImagePath = FileUtils(this).getPath(fileUri)
                    val uri: Uri = data?.data!!
                    val file = File(PathUtil.getPath(this, data.data))
                    binding.civProfileImage.setImageURI(uri)
                    profileAPI(file)
                }
                ImagePicker.RESULT_ERROR -> toast(ImagePicker.getError(data))
                else -> toast("Task Cancelled")
            }
        }
    }

    private fun profileAPI(file: File) {
        val builder = file.multipartImageBody("image[]")
        builder.addFormDataPart("dir", "profile_picture")
        viewModel.uploadJobImage(builder.build())
    }

    private fun subCategoryLis(id: String) {
        val category = Category(id)
        viewModel.getSubCategory(category)
    }

    @ObsoleteCoroutinesApi
    private fun getBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey("ADDRESS_CHANGE")) {
                Timber.e("ADDRESS_CHANGE")
                val ADDRESS = it.getBoolean("ADDRESS_CHANGE", false)
                val companyAddress = it.getBoolean("companyAddress", false)
                val aDDRESSEdit = it.getString("ADDRESS")
                val city = it.getString("city")
                val state = it.getString("state")
                val zipcode = it.getString("zipCode")

                if (ADDRESS) {
                    binding.etAddress.setText(aDDRESSEdit)
                    binding.spSelectCity.setText(city)
                    binding.spSelectState.setText(state)
                    binding.etZipCode.setText(zipcode)
                    mapCityName = city
                    mapStateName = state
                    mapZipCode = zipcode
                    binding.etCompanyAddress.setText(aDDRESSEdit)
                } else if (companyAddress) {
                    binding.etCompanyAddress.setText(aDDRESSEdit)
                }
            }
        }
    }
}






