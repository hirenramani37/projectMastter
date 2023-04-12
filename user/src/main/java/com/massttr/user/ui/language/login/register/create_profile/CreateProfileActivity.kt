package com.massttr.user.ui.language.login.register.create_profile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.common.base.BaseActivity
import com.common.data.network.model.request.CreateProfile
import com.github.dhaval2404.imagepicker.ImagePicker
import com.massttr.user.R
import com.massttr.user.databinding.ActivityCreateProfileBinding
import com.massttr.user.ui.language.login.register.create_profile.location.LocationActivity
import com.massttr.user.ui.language.login.register.create_profile.success_create_profile.SuccessfullyActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import java.io.File
import java.util.*

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class CreateProfileActivity :
    BaseActivity<ActivityCreateProfileBinding>(R.layout.activity_create_profile),
    View.OnClickListener {
    private val viewModel: CreateProfileActivityViewModel by viewModels()
    private lateinit var file: File
    private var cal = Calendar.getInstance()
    private var profileImagePath: String = ""
    var latitudeAddress: String = ""
    var longitudeAddress: String = ""

    companion object {
        private const val PICKER_IMAGE_PROFILE = 100
        const val FULL_NAME = "FULL_NAME"
        const val MOBILE_NUMBER = "MOBILE_NUMBER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setUpObserver()
        getBusEvent()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@CreateProfileActivity) { handleError(it) }
            appLoader.observe(this@CreateProfileActivity) { updateLoaderUI(it) }
            create.observe(this@CreateProfileActivity) {
                pref.isLogin = true
//                pref.authToken = it.data?.device_token
                pref.userInfo = it.data
                startActivity<SuccessfullyActivity>()
            }

            uploadProfileImage.observe(this@CreateProfileActivity) {
                if (it.success) {
                    getString(R.string.successfully).showSuccessToast()
                    it.data.forEach { imagePath ->
                        profileImagePath = imagePath
                    }
                }
            }
        }
    }

    private fun initView() {
        AppGlobal.getFcmToken(this) {}
        binding.run {
            toolbar.tvTitle.text = getString(R.string.create_profile)
            if (intent.hasExtra(FULL_NAME))
                etFullName.setText(intent.getStringExtra(FULL_NAME) ?: "")

            if (intent.hasExtra(MOBILE_NUMBER))
                tvMobileNu.text = intent.getStringExtra(MOBILE_NUMBER) ?: ""
        }
    }

    private fun clickListener() {
        binding.run {
            btnProfileDone.setOnClickListener(this@CreateProfileActivity)
            tvDOB.setOnClickListener(this@CreateProfileActivity)
            ivEditProfile.setOnClickListener(this@CreateProfileActivity)
            btnLocation.setOnClickListener(this@CreateProfileActivity)
            toolbar.imgBack.setOnClickListener(this@CreateProfileActivity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnProfileDone -> validate()
            R.id.btnLocation -> startActivity<LocationActivity>()
            R.id.tvDOB -> {
                val dateSetListener =
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        binding.tvDOB.text =
                            getString(R.string.selected_Date, year, (monthOfYear + 1), dayOfMonth)
                    }

                DatePickerDialog(
                    this@CreateProfileActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.ivEditProfile -> {
                ImagePicker.with(this)
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start(PICKER_IMAGE_PROFILE)
            }
        }
    }

    private fun validate() {
        binding.run {
            when {
                etFullName.isBlank() -> setShakeError(getString(R.string.please_enter_first_name))
                tvMobileNu.text.isBlank() -> setShakeError(getString(R.string.please_enter_number))
                etEmailId.isBlank() -> setShakeError(getString(R.string.please_enter_number))
                !etEmailId.text.toString().isEmailValid -> setShakeError("Please Enter Valid Email")
                tvDOB.text.isBlank() -> setShakeError(getString(R.string.please_enter_dob))
                etAddress.isBlank() -> setShakeError(getString(R.string.please_enter_address))
                rvGender.checkedRadioButtonId == -1 -> setShakeError("Please select gender")
                pref.fcmToken.isNullOrEmpty() -> AppGlobal.getFcmToken(this@CreateProfileActivity) { createProfile() }
                else -> createProfile()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICKER_IMAGE_PROFILE -> {
                    val uri: Uri = data?.data!!
                    file = File(PathUtil.getPath(this, data.data))
                    binding.civProfile.setImageURI(uri)
                    profileAPI(file)
                }
            }
        }
    }

    private fun profileAPI(file: File) {
        val builder = file.multipartImageBody("image[]")
        builder.addFormDataPart("dir", "profile_picture")
        viewModel.uploadJobImage(builder.build())
    }

    @ObsoleteCoroutinesApi
    private fun getBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey("ADDRESS_CHANGE")) {
                if (it.getBoolean("ADDRESS_CHANGE", false)) {
                    binding.etAddress.setText(it.getString("ADDRESS"))
                    val latitude = it.getString("latitude")
                    val longitude = it.getString("longitude")
                    latitudeAddress = latitude ?: ""
                    longitudeAddress = longitude ?: ""
                }
            }
        }
    }

    private fun createProfile() {
        binding.run {
            viewModel.createProfile(
                CreateProfile(
                    if (pref.isLanguage == true) "en" else "ar",
                    etFullName.getTextString(),
                    "964",
                    tvMobileNu.text.toString(),
                    tvDOB.text.toString(),
                    etEmailId.getTextString(),
                    if (rbMale.isChecked) 1 else 2,
                    etZip.getTextString(),
                    etAddress.getTextString(),
                    pref.fcmToken.toString(),
                    profileImagePath,
                    "A",
                    latitudeAddress,
                    longitudeAddress
                )
            )
        }
    }
}
