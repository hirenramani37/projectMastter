package com.massttr.user.ui.main.home.taskrequest

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.common.base.BaseActivity
import com.common.data.network.model.Subcategory
import com.common.data.network.model.TaskBookJob
import com.github.dhaval2404.imagepicker.ImagePicker
import com.massttr.user.R
import com.massttr.user.databinding.ActivityTaskRequestBinding
import com.massttr.user.databinding.RequestTaskCancelDialogBinding
import com.massttr.user.ui.main.home.taskrequest.subCategory.SubCategoryActivity
import com.massttr.user.ui.main.home.taskrequest.task_details.TaskDetailsActivity
import com.massttr.user.utils.*
import com.michaldrabik.classicmaterialtimepicker.CmtpTimeDialogFragment
import com.michaldrabik.classicmaterialtimepicker.model.CmtpTime12
import com.michaldrabik.classicmaterialtimepicker.utilities.setOnTime12PickedListener
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class TaskRequestActivity :
    BaseActivity<ActivityTaskRequestBinding>(R.layout.activity_task_request), View.OnClickListener {

    private val viewModel: TaskRequestActivityViewModel by viewModels()
    private var taskRequestAdapter: TaskRequestAdapter? = null
    private var selectTaskPhotos: SelectPhotoTaskAdapter? = null
    private val subCategoryList = ArrayList<Subcategory>()
    private var subCateArrayList = ArrayList<Subcategory>()
    private var subcategoryAddId = ArrayList<Int>()
    private val multiTaskImage = ArrayList<String>()
    private var taskImagePath: ArrayList<File> = ArrayList(3)
    private var temp = File("")
    private lateinit var bannerFile: File
    private lateinit var taskBookJob: TaskBookJob
    private var bannerImage = ""
    private var uri: Uri? = null
    private var position: Int = 0
    private var fullTaskPrice = false
    private var toolRequest = false
    private var timeRequest = false
    private var priceType: Int = 0
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var address: String
    private var categorySelect = 0
    private var filterList = ArrayList<File>()

    companion object {
        private const val REQUEST_CODE_SUB_CATEGORY = 1
        private const val PICKER_CODE_BANNER_IMAGE = 77
        private const val PICKER_CODE_GALLERY_IMAGE = 79
        private const val ONE_TASK_GALLERY_IMAGE = 11
        private const val TWO_TASK_GALLERY_IMAGE = 22
        private const val THREE_TASK_GALLERY_IMAGE = 33
        private const val FOUR_CODE_GALLERY_IMAGE = 44
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
        const val ADDRESS = "ADDRESS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedTab(0)
        setUpUI()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            category()
            apiErrors.observe(this@TaskRequestActivity) { handleError(it) }
            appLoader.observe(this@TaskRequestActivity) { updateLoaderUI(it) }
            category.observe(this@TaskRequestActivity) { it ->
                it.data.let { data ->
                    binding.spCategory.setUpSpinnerCategory(
                        it.data?.map { it.en_name } as ArrayList<String>, ""
                    )

                    binding.spCategory.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>, view: View,
                                pos: Int, id: Long,
                            ) {
                                categorySelect = data?.get(pos)?.id ?: 0
                                //categorySelect = parent.getItemAtPosition(pos) as Int
                            }

                            override fun onNothingSelected(arg0: AdapterView<*>?) {}
                        }
                }
            }
            uploadJobImageMultiple.observe(this@TaskRequestActivity) {
                if (it.success) {
                    it.data.forEach { uploadImage ->
                        multiTaskImage.add(uploadImage)
                    }
                    binding.run {
                        taskBookJob =
                            TaskBookJob(
                                bannerFile,
                                etTaskTitle.getTextString(),
                                subCateArrayList,
                                filterList,
                                etTaskDescription.getTextString(),
                                tvDateTimeLine.text.toString(),
                                tvTimeTimeLine.text.toString(),
                                etHourlyRate.getTextString(),
                                etHours.getTextString(),
                                tvDkkPrice.text.toString(),
                                priceType,
                                etRequiredTool.getTextString(),
                                address,
                                latitude,
                                longitude,
                                spCategory.selectedItemPosition,
                                subcategoryAddId.toString().removeBrackets(),
                            )
                    }

                    startActivity<TaskDetailsActivity>(
                        TaskDetailsActivity.TASK_BOOK_JOB to taskBookJob,
                        TaskDetailsActivity.BANNER_IMAGE to bannerImage,
                        TaskDetailsActivity.MULTIPLE_IMAGE to multiTaskImage
                    )
                }
            }

            uploadBannerImage.observe(this@TaskRequestActivity) {
                if (it.success)
                    it.data.forEach { banner ->
                        bannerImage = banner
                    }
            }
        }
    }

    private fun setUpUI() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.task_request)
            if (intent.hasExtra(LATITUDE)) latitude = intent.getStringExtra(LATITUDE).toString()
            if (intent.hasExtra(LONGITUDE)) longitude = intent.getStringExtra(LONGITUDE).toString()
            if (intent.hasExtra(ADDRESS)) address = intent.getStringExtra(ADDRESS).toString()

            taskImagePath.addAll(listOf(temp, temp, temp, temp))

            taskRequestAdapter = TaskRequestAdapter()
            rvSubCategory.adapter = taskRequestAdapter
        }
    }

    private fun selectedColor(
        @ColorInt color: Int = ContextCompat.getColor(
            this@TaskRequestActivity,
            R.color.colorPrimaryDark
        ),
    ): ColorStateList {
        return ColorStateList.valueOf(color)
    }

    private fun unselectedColor(
        @ColorInt color: Int = ContextCompat.getColor(
            this@TaskRequestActivity,
            R.color.tex_grayTwo
        ),
    ): ColorStateList {
        return ColorStateList.valueOf(color)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvNext -> {
                v.closeSoftKeyboard()
                when (position) {
                    0 -> if (validateFirst()) {
                        position++
                        selectedTab(position)
                    }
                    1 -> {
                        position++
                        selectedTab(position)
                    }
                    2 -> if (fullTaskPrice) {
                        if (validateSecond()) {
                            position++
                            selectedTab(position)
                        }
                    } else if (validateThird()) {
                        position++
                        selectedTab(position)
                    }
                    3 -> if (validateFourth()) {
                        position++
                        selectedTab(position)
                    }
                    4 -> {
                        position++
                        selectedTab(position)
                        startActivity<TaskDetailsActivity>()
                        finish()
                    }
                    5 -> apiCalling()
                }
            }
            R.id.tvPrevious -> {
                position--
                selectedTab(position)
            }
            R.id.tvYesPayment -> {
                fullTaskPrice = true
                selectedTab(2)
                position = 2
                priceType = 0
                binding.tvPaymentTitle.text = getString(R.string.full_task_price)
            }
            R.id.tvNoPayment -> { // hourly price
                fullTaskPrice = false
                selectedTab(2)
                position = 2
                priceType = 1
                binding.tvPaymentTitle.text = getString(R.string.hourly_rate)
            }
            R.id.tvTimeTimeLine -> {
                val timePicker = CmtpTimeDialogFragment.newInstance()
                timePicker.setInitialTime12(5, 15, CmtpTime12.PmAm.PM)
                timePicker.show(supportFragmentManager, "Tag")
                timePicker.setOnTime12PickedListener { time12 ->
                    binding.tvTimeTimeLine.text = ("${time12.hour}:${time12.minute} ${time12.pmAm}")
                }
            }
            R.id.tvDateTimeLine -> {
                val c = Calendar.getInstance()
                val dpd = DatePickerDialog(
                    this,
                    { _, year, month, date ->
                        binding.tvDateTimeLine.text =
                            getString(R.string.selected_Date, year, (month + 1), date)
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DATE)
                )
                dpd.datePicker.minDate = System.currentTimeMillis()
                dpd.show()
            }
            R.id.tvToolNo -> {
                toolRequest = false
                position = 5
                selectedTab(5)
            }
            R.id.tvToolYes -> {
                toolRequest = true
                position = 5
                selectedTab(5)
            }
            R.id.ivAddCategory -> {
                val intent = Intent(this, SubCategoryActivity::class.java)
                intent.putExtra(SubCategoryActivity.SUB_CATEGORY_LIST, subCategoryList)
                intent.putExtra(SubCategoryActivity.PRM_CATEGORY_EDIT_TEXT, categorySelect)
                startActivityForResult(intent, REQUEST_CODE_SUB_CATEGORY)
            }
            R.id.btnCurrentDateTime -> getCurrentDateTime()
            R.id.ivTakePhoto -> pickImage(PICKER_CODE_BANNER_IMAGE)
            R.id.llSelectPhotoOne -> pickImage(ONE_TASK_GALLERY_IMAGE)
            R.id.llSelectPhotoTwo -> pickImage(TWO_TASK_GALLERY_IMAGE)
            R.id.llSelectPhotoThree -> pickImage(THREE_TASK_GALLERY_IMAGE)
            R.id.llSelectPhotoFours -> pickImage(FOUR_CODE_GALLERY_IMAGE)
            R.id.imgBack -> endTaskRequestDialog()

        }
    }

    private fun endTaskRequestDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        val binding: RequestTaskCancelDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.request_task_cancel_dialog,
            null,
            false
        )

        binding.tvOk.setOnClickListener {
            onBackPressed()
            dialog.dismiss()
        }

        binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun getCurrentDateTime() {
        binding.run {
            tvDateTimeLine.text =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Calendar.getInstance().time).toString()
            tvTimeTimeLine.text =
                SimpleDateFormat(
                    "HH:mm aa",
                    Locale.getDefault()
                ).format(Calendar.getInstance().time).toString()
        }
    }

    private fun apiCalling() {
        binding.run {
            filterList = taskImagePath.filter { it.length() > 0 } as ArrayList<File>
            if (filterList.isEmpty()) {
                taskBookJob = TaskBookJob(
                    bannerFile,
                    etTaskTitle.getTextString(),
                    subCateArrayList,
                    filterList,
                    etTaskDescription.getTextString(),
                    tvDateTimeLine.text.toString(),
                    tvTimeTimeLine.text.toString(),
                    etHourlyRate.getTextString(),
                    etHours.getTextString(),
                    tvDkkPrice.text.toString(),
                    priceType,
                    etRequiredTool.getTextString(),
                    address,
                    latitude,
                    longitude,
                    spCategory.selectedItemPosition,
                    subcategoryAddId.toString().removeBrackets(),
                )
                startActivity<TaskDetailsActivity>("TASK_BOOK_JOB" to taskBookJob)
            } else {
                val builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)
                for (it in filterList.indices) {
                    builder.addFormDataPart(
                        "image[]",
                        filterList[it].name.replace("-", "_"),
                        filterList[it].asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    builder.addFormDataPart("dir", "task_picture")
                }
                viewModel.uploadJobImageMultiple(builder.build())
            }
        }
    }

    private fun pickImage(code: Int) {
        ImagePicker.with(this)
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_SUB_CATEGORY -> {
                    subCateArrayList =
                        data?.getSerializableExtra(SubCategoryActivity.RESULT) as ArrayList<Subcategory>
                    subCateArrayList.forEach {
                        if (it.isSelected) {
                            it.id?.let { it1 -> subcategoryAddId.add(it1) }
                        }
                    }
                    binding.tvSubcategory.setTextAppearance(R.style.spinnerBlack)
                    taskRequestAdapter?.addAll(subCateArrayList)
                }
                PICKER_CODE_BANNER_IMAGE -> {
                    uri = data?.data!!
                    bannerFile = File(PathUtil.getPath(this, data.data))
                    binding.ivTakePhoto.setImageURI(uri)
                    uploadBannerImage(bannerFile)
                }
                ONE_TASK_GALLERY_IMAGE -> {
                    uri = data?.data!!
                    val file = File(PathUtil.getPath(this, data.data))
                    taskImagePath[0] = file
                    binding.tvImageOne.gone()
                    binding.ivPhotoTaskOne.setImageURI(uri)
                    //temp = file
                }
                TWO_TASK_GALLERY_IMAGE -> {
                    uri = data?.data!!
                    val file = File(PathUtil.getPath(this, data.data))
                    taskImagePath[1] = file
                    binding.tvImageTwo.gone()
                    binding.ivPhotoTaskTwo.setImageURI(uri)
                    //temp = file
                }
                THREE_TASK_GALLERY_IMAGE -> {
                    uri = data?.data!!
                    val file = File(PathUtil.getPath(this, data.data))
                    taskImagePath[2] = file
                    binding.tvImageThree.gone()
                    binding.ivPhotoTaskThree.setImageURI(uri)
                    //temp = file
                }
                FOUR_CODE_GALLERY_IMAGE -> {
                    uri = data?.data!!
                    val file = File(PathUtil.getPath(this, data.data))
                    taskImagePath[3] = file
                    binding.tvImageFour.gone()
                    binding.ivPhotoTaskFours.setImageURI(uri)
                    //temp = file
                }
            }
        }
    }

    private fun uploadBannerImage(file: File) {
        val builder = file.multipartImageBody("image[]")
        builder.addFormDataPart("dir", "banner_image")
        viewModel.uploadJobBanner(builder.build())
    }

    private fun validateFourth(): Boolean {
        binding.run {
            when {
                tvDateTimeLine.text.isBlank() -> tvDateTimeLine.setShakeError(getString(R.string.choose_date))
                tvTimeTimeLine.text.isBlank() -> tvTimeTimeLine.setShakeError(getString(R.string.choose_time))
                else -> return true
            }
            return false
        }
    }

    private fun validateThird(): Boolean {
        binding.run {
            when {
                etHourlyRate.text.toString()
                    .isEmpty() -> etHourlyRate.setShakeError(getString(R.string.enter_hourly_rate))
                etHours.text.toString()
                    .isEmpty() -> etHours.setShakeError(getString(R.string.enter_hours))
                else -> return true
            }
        }
        return false
    }

    private fun validateSecond(): Boolean {
        when {
            binding.etFullTaskPrice.isBlank() -> binding.etFullTaskPrice.setShakeError(getString(R.string.enter_full_task_price))
            else -> return true
        }
        return false
    }

    private fun validateFirst(): Boolean {
        binding.run {
            when {
                etTaskTitle.text.isBlank() -> etTaskTitle.setShakeError(getString(R.string.enter_task_title))
                etTaskDescription.text.isBlank() -> etTaskDescription.setShakeError(getString(R.string.enter_task_disc))
//            spCategory.isEmpty() -> spCategory.setShakeError(getString(R.string.enter_task_title))
                tvSubcategory.text.toString()
                    .isEmpty() -> tvSubcategory.setShakeError(getString(R.string.please_select_category))
                else -> return true
            }
        }
        return false
    }

    private fun selectedTab(index: Int) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        when (index) {
            0 -> {
                binding.run {
                    tvOne.backgroundTintList = selectedColor()
                    viewOne.backgroundTintList = unselectedColor()
                    tvTwo.backgroundTintList = unselectedColor()
                    viewTwo.backgroundTintList = unselectedColor()
                    tvThree.backgroundTintList = unselectedColor()
                    viewThree.backgroundTintList = unselectedColor()
                    tvFour.backgroundTintList = unselectedColor()
                    llTaskOne.visible()
                    llPriceAsk.gone()
                    llTaskTwo.gone()
                    tvNext.visible()
                    tvPrevious.gone()
                    llTimeLine.gone()
                }
            }
            1 -> {  // payment ask
                binding.run {
                    tvOne.backgroundTintList = selectedColor()
                    viewOne.backgroundTintList = selectedColor()
                    tvTwo.backgroundTintList = selectedColor()
                    viewTwo.backgroundTintList = unselectedColor()
                    tvThree.backgroundTintList = unselectedColor()
                    viewThree.backgroundTintList = unselectedColor()
                    tvFour.backgroundTintList = unselectedColor()

                    tvPaymentTitle.text = getString(R.string.payment)
                    llTaskOne.gone()
                    llTaskTwo.visible()
                    llTimeLine.gone()
                    llPriceAsk.visible()
                    llWhichTaskPrice.visible()
                    tvSubTitle.visible()
                    llAskHourly.gone()
                    etFullTaskPrice.gone()
                    llRequest.gone()
                    llPriceAsk.visible()
                    tvNext.gone()
                    tvPrevious.visible()
                    tvTotal.gone()
                }
            }
            2 -> {   // full or hourly
                binding.run {
                    tvOne.backgroundTintList = selectedColor()
                    viewOne.backgroundTintList = selectedColor()
                    tvTwo.backgroundTintList = selectedColor()
                    viewTwo.backgroundTintList = unselectedColor()
                    tvThree.backgroundTintList = unselectedColor()
                    viewThree.backgroundTintList = unselectedColor()
                    tvFour.backgroundTintList = unselectedColor()
                    llTimeLine.gone()
                    llWhichTaskPrice.gone()
                    tvSubTitle.gone()
                    tvTotal.visible()
                    llPriceAsk.visible()
                    ivPaymentPlace.visible()
                    tvPaymentTitle.visible()
                    tvNext.visible()
                    tvPrevious.visible()
                    llTaskTwo.visible()

                    if (timeRequest) {
                        llPriceAsk.visible()
                        tvTotal.gone()
                    }

                    if (fullTaskPrice) {  //yes
                        llAskHourly.gone()
                        etFullTaskPrice.visible()
                        llTaskTwo.visible()
                    } else {   //no
                        etFullTaskPrice.gone()
                        llAskHourly.visible()
                    }
                    calculationPrice()
                }
            }
            3 -> {   // timeLine
                binding.run {
                    tvOne.backgroundTintList = selectedColor()
                    viewOne.backgroundTintList = selectedColor()
                    tvTwo.backgroundTintList = selectedColor()
                    viewTwo.backgroundTintList = selectedColor()
                    tvThree.backgroundTintList = unselectedColor()
                    viewThree.backgroundTintList = unselectedColor()
                    tvFour.backgroundTintList = unselectedColor()
                    llTaskOne.gone()
                    llPriceAsk.gone()
                    llTaskTwo.gone()
                    llTimeLine.visible()
                    tvPrevious.visible()
                    tvNext.visible()
                    tvTotal.gone()
                    llRequest.gone()
                }
            }
            4 -> {
                binding.run { // toolask
                    tvOne.backgroundTintList = selectedColor()
                    viewOne.backgroundTintList = selectedColor()
                    tvTwo.backgroundTintList = selectedColor()
                    viewTwo.backgroundTintList = selectedColor()
                    tvThree.backgroundTintList = selectedColor()
                    viewThree.backgroundTintList = unselectedColor()
                    tvFour.backgroundTintList = unselectedColor()
                    llTaskOne.gone()
                    llPriceAsk.gone()
                    llRequest.visible()
                    llToolAsk.visible()
                    llTimeLine.gone()
                    tvPrevious.visible()
                    tvNext.gone()
                    tvTotal.gone()
                    llUploadRequest.gone()
                }
            }
            5 -> {  // toolrequest
                binding.run {
                    tvOne.backgroundTintList = selectedColor()
                    viewOne.backgroundTintList = selectedColor()
                    tvTwo.backgroundTintList = selectedColor()
                    viewTwo.backgroundTintList = selectedColor()
                    tvThree.backgroundTintList = selectedColor()
                    viewThree.backgroundTintList = selectedColor()
                    tvFour.backgroundTintList = selectedColor()
                    tvNext.text = getString(R.string.confirm_details)
                    llToolRequest.gone()
                    llPriceAsk.gone()
                    tvNext.visible()
                    llUploadRequest.visible()
                    llTaskOne.gone()
                    llPriceAsk.gone()
                    llRequest.visible()
                    llToolAsk.gone()
                    llTimeLine.gone()
                    tvPrevious.visible()
                    tvTotal.gone()
                    tvTitleRequest.visible()

                    if (toolRequest) {
                        llToolRequest.visible()
                        tvTitleRequest.visible()
                    } else {
                        llToolRequest.gone()
                        tvTitleRequest.gone()
                    }
                }
            }
        }
    }

    private fun calculationPrice() {
        binding.etFullTaskPrice.addTextChangedListener {
            try {
                val v1: Double =
                    if (binding.etFullTaskPrice.getTextString().isNotEmpty())
                        binding.etFullTaskPrice.text.toString().toDouble()
                    else
                        "0".toDouble()
                val value = (v1.toString().toDouble() * "1".toDouble())
                binding.tvDkkPrice.text = value.toString()
            } catch (ex: NumberFormatException) {
                Timber.e("$ex it not number")
            }
        }

        binding.etHourlyRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                try {
                    val v1: Double =
                        if (binding.etHourlyRate.text.toString().isNotEmpty()) {
                            binding.etHourlyRate.text.toString().toDouble()
                        } else {
                            "0".toDouble()
                        }
                    val v2: Double =
                        if (binding.etHours.text.toString().isNotEmpty()) {
                            binding.etHours.text.toString().toDouble()
                        } else {
                            "0".toDouble()
                        }
                    val value = (v1 * v2)
                    binding.tvDkkPrice.text = value.toString()
                } catch (ex: NumberFormatException) {
                    Timber.e("$ex it not number")
                }
            }
        })

        binding.etHours.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                try {
                    val v1: Double =
                        if (binding.etHourlyRate.text.toString().isNotEmpty()) {
                            binding.etHourlyRate.text.toString().toDouble()
                        } else {
                            "0".toDouble()
                        }
                    val v2: Double =
                        if (binding.etHours.text.toString().isNotEmpty()) {
                            binding.etHours.text.toString().toDouble()
                        } else {
                            "0".toDouble()
                        }
                    val value = (v1 * v2)
                    binding.tvDkkPrice.text = value.toString()
                } catch (ex: NumberFormatException) {
                    Timber.e("$ex it not number")
                }
            }
        })
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@TaskRequestActivity)
            tvNext.setOnClickListener(this@TaskRequestActivity)
            tvPrevious.setOnClickListener(this@TaskRequestActivity)
            tvYesPayment.setOnClickListener(this@TaskRequestActivity)
            tvNoPayment.setOnClickListener(this@TaskRequestActivity)
            tvDateTimeLine.setOnClickListener(this@TaskRequestActivity)
            tvTimeTimeLine.setOnClickListener(this@TaskRequestActivity)
            tvToolYes.setOnClickListener(this@TaskRequestActivity)
            tvToolNo.setOnClickListener(this@TaskRequestActivity)
            ivTakePhoto.setOnClickListener(this@TaskRequestActivity)
            rvPhotoOfTheTask.setOnClickListener(this@TaskRequestActivity)
            ivAddCategory.setOnClickListener(this@TaskRequestActivity)
            btnCurrentDateTime.setOnClickListener(this@TaskRequestActivity)

            // Select Image
            llSelectPhotoOne.setOnClickListener(this@TaskRequestActivity)
            llSelectPhotoTwo.setOnClickListener(this@TaskRequestActivity)
            llSelectPhotoThree.setOnClickListener(this@TaskRequestActivity)
            llSelectPhotoFours.setOnClickListener(this@TaskRequestActivity)

            //click event
            selectTaskPhotos?.setItemClickListener { view, position, _ ->
                when (view.id) {
                    R.id.ivPhotoTask -> {
                    }
                    R.id.llSelectPhoto -> if (position == 0) pickImage(PICKER_CODE_GALLERY_IMAGE)
                }
            }
        }
    }
}




