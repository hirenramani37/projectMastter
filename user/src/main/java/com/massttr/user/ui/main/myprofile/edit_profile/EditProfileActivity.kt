package com.massttr.user.ui.main.myprofile.edit_profile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.request.UpdateProfile
import com.github.dhaval2404.imagepicker.ImagePicker
import com.massttr.user.R
import com.massttr.user.databinding.ActivityEditProfileBinding
import com.massttr.user.ui.language.login.register.create_profile.location.LocationActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.io.File
import java.util.*

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class EditProfileActivity :
    BaseActivity<ActivityEditProfileBinding>(R.layout.activity_edit_profile),
    View.OnClickListener {

    private val viewModel: EditProfileActivityViewModel by viewModels()
    private lateinit var file: File
    private var profileImagePath: String = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setUpObserver()
        clickListener()
        getBusEvent()
    }

    companion object {
        private const val PICKER_IMAGE_PROFILE = 100
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.my_profile)
            pref.userInfo.let {
                it?.profile_picture?.let { image ->
                    this@EditProfileActivity.loadImages(
                        image,
                        civProfile, R.drawable.ic_placeholder
                    )
                }
                etFullName.setText(it?.full_name)
                etMobileNumber.setText(it?.mobile_no)
                etEmailId.setText(it?.email)
                tvDateDOB.text = it?.dob
                if (it?.gender == 1) rbMale.isChecked = true else rbFemale.isChecked = true
                etZipCode.setText(it?.zipcode)
                etAddress.setText(it?.address)
            }
        }
        viewModel.editProfile()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@EditProfileActivity) { handleError(it) }
            appLoader.observe(this@EditProfileActivity) { updateLoaderUI(it) }
            updateProfile.observe(this@EditProfileActivity) {
                pref.userInfo = it.data
                val bundle = Bundle()
                bundle.putBoolean("UpdateProfile", true)
                EventBus.publish(bundle)
                onBackPressed()
            }
            uploadProfileImage.observe(this@EditProfileActivity) {
                if (it.success) {
                    it.data.forEach { imagePath ->
                        profileImagePath = imagePath
                    }
                }
            }
            editProfile.observe(this@EditProfileActivity) {
                if (it.success) {
                    longitude = it.data.long
                    latitude = it.data.lat
                }
            }
        }
    }

    private fun clickListener() {
        binding.run {
            btnUpDateProfile.setOnClickListener(this@EditProfileActivity)
            tvDateDOB.setOnClickListener(this@EditProfileActivity)
            toolbar.imgBack.setOnClickListener(this@EditProfileActivity)
            civProfile.setOnClickListener(this@EditProfileActivity)
            btnLocation.setOnClickListener(this@EditProfileActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnUpDateProfile -> validate()
            R.id.tvDateDOB -> {
                val dpd = DatePickerDialog(
                    this, { _, year, month, date ->
                        binding.tvDateDOB.text =
                            getString(R.string.selected_Date, year, (month + 1), date)
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DATE)
                )
                dpd.datePicker.minDate = System.currentTimeMillis()
                dpd.show()
            }
            R.id.civProfile -> ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(PICKER_IMAGE_PROFILE)
            R.id.imgBack -> onBackPressed()
            R.id.btnLocation -> startActivity<LocationActivity>("lat" to latitude, "log" to longitude)
        }
    }

    private fun validate() =
        binding.run {
            when {
                etFullName.isBlank() -> setShakeError(getString(R.string.please_enter_first_name))
                etMobileNumber.isBlank() -> setShakeError(getString(R.string.please_enter_number))
                tvDateDOB.text.isBlank() -> setShakeError(getString(R.string.please_enter_dob))
                llGender.checkedRadioButtonId == -1 -> setShakeError("Please select Gender")
                etZipCode.isBlank() -> setShakeError(getString(R.string.please_enter_zip))
                etAddress.isBlank() -> setShakeError(getString(R.string.please_enter_address))
                else -> {
                    viewModel.updateProfile(
                        UpdateProfile(
                            etFullName.getTextString(),
                            964,
                            etMobileNumber.getTextString(),
                            etZipCode.getTextString(),
                            etAddress.getTextString(),
                            profileImagePath,
                            "",
                            "",
                            "",
                            0,
                            if (rbMale.isChecked) 1 else 0,
                            tvDateDOB.text.toString(),
                            etEmailId.getTextString(),
                            latitude,
                            longitude
                        )
                    )
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


    private fun getBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey("ADDRESS_CHANGE")) {
                Timber.e("ADDRESS_CHANGE")
                val address = it.getBoolean("ADDRESS_CHANGE", false)
                val aDDRESSEdit = it.getString("ADDRESS")
                val lat = it.getString("latitude")
                val long = it.getString("longitude")

                if (address) {
                    binding.etAddress.setText(aDDRESSEdit)
                    longitude = long?.toDouble() ?: 0.0
                    latitude = lat?.toDouble() ?: 0.0
                    // binding.spCity.setText(city)
                    // binding.spState.setText(state)
                }
            }
        }
    }

    private fun profileAPI(file: File) {
        val builder = file.multipartImageBody("image[]")
        builder.addFormDataPart("dir", "profile_picture")
        viewModel.uploadJobImage(builder.build())
    }
}
