package com.massttr.provider.ui.main.myprofiles.editProfile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import coil.load
import com.common.base.BaseActivity
import com.common.data.network.model.request.UserProfileUpdate
import com.massttr.user.utils.EventBus
import com.massttr.user.utils.PathUtil
import com.massttr.user.utils.multipartImageBody
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.tabs.TabLayoutMediator
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber
import java.io.File


@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class EditProfileActivity :
    BaseActivity<ActivityEditProfileBinding>(R.layout.activity_edit_profile), View.OnClickListener {

    private val viewModel: EditProfileViewModel by viewModels()
    private lateinit var file: File
    private var profileImagePath: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBusEvent()
        setUpUI()
        setUpObserver()
        clickListener()
    }

    companion object {
        private const val PICKER_IMAGE_PROFILE = 100
    }

    private fun clickListener() {
        binding.run {
            toolBar.imgBack.setOnClickListener(this@EditProfileActivity)
            btnUpdateProfile.setOnClickListener(this@EditProfileActivity)
            civProfile.setOnClickListener(this@EditProfileActivity)
        }
    }

    private fun setUpUI() {
        binding.run {
            toolBar.tvTitle.text = getString(R.string.my_profile)

            pref.userInfo.let { civProfile.load(it?.profile_picture){
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            } }
            vpPager.adapter = EditProfileViewPagerAdapter(this@EditProfileActivity)
            TabLayoutMediator(tlLeader, vpPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.personal_details)
                    1 -> tab.text = getString(R.string.company_details)
                }
            }.attach()
        }
        viewModel.getProfile()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@EditProfileActivity) { handleError(it) }
            appLoader.observe(this@EditProfileActivity) { updateLoaderUI(it) }
            getProfile.observe(this@EditProfileActivity) {
                //pref.userInfo = it
            }

            editProfile.observe(this@EditProfileActivity) {
                val userInfo = pref.userInfo
                userInfo?.first_name = it.data?.first_name.toString()
                userInfo?.last_name = it.data?.last_name.toString()
                userInfo?.email_id = it.data?.email_id.toString()
                userInfo?.phone_no = it.data?.phone_no.toString()
                userInfo?.country_code = 964
                userInfo?.zipcode = it.data?.zipcode.toString()
                userInfo?.address = it.data?.address.toString()
                userInfo?.personal = false
                userInfo?.company_name = it.data?.company_name.toString()
                userInfo?.company_vat_no = it.data?.company_vat_no.toString()
                userInfo?.email_id = it.data?.email_id.toString()
                userInfo?.company_zipcode = it.data?.company_zipcode.toString()
                userInfo?.company_address = it.data?.company_address.toString()
                userInfo?.city = it.data?.city.toString()
                userInfo?.profile_picture = it.data?.profile_picture?:""
                userInfo?.company = true
                pref.userInfo = userInfo
                onBackPressed()
                val bundle = Bundle()
                bundle.putBoolean("UpdateProfile", true)
                EventBus.publish(bundle)
            }

            uploadProfileImage.observe(this@EditProfileActivity) {
                if (it.success) {
                    it.data.forEach { imagePath ->
                        profileImagePath = imagePath
                    }
                }
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnUpdateProfile -> {
                val bundle = Bundle()
                bundle.putBoolean("UpdateProfile", true)
                EventBus.publish(bundle)
            }
            R.id.civProfile -> ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(PICKER_IMAGE_PROFILE)
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
            if (it.containsKey("Profile")) {
                Timber.e("Profile")
                val updateProfile = it.getInt("Profile", 0)
                if (updateProfile == 0) { // Personal Details
                    binding.vpPager.currentItem = 1
                } else {
                    binding.vpPager.currentItem = 0
                }
                if (pref.userInfo?.company == true && pref.userInfo?.personal == true) {
                    val userInfo = pref.userInfo
                    userInfo?.personal = false
                    userInfo?.company = false
                    pref.userInfo = userInfo
                    Timber.e("ProfileBoth")
                    viewModel.editProfile(UserProfileUpdate(
                        profileImagePath,
                        userInfo?.full_name.toString(),
                        userInfo?.country_code.toString(),
                        userInfo?.phone_no.toString(),
                        userInfo?.email_id.toString(),
                        userInfo?.birth_date.toString(),
                        userInfo?.zipcode.toString(),
                        userInfo?.address.toString(),
                        userInfo?.company_name.toString(),
                        userInfo?.company_vat_no.toString(),
                        userInfo?.company_email.toString(),
                        userInfo?.company_zipcode.toString(),
                        userInfo?.company_address.toString(),
                        userInfo?.is_send_invoice.toString(),
                        "1",
                        "1",
                        "test",
                        userInfo?.gender.toString(),
                        userInfo?.city.toString(),
                        userInfo?.state.toString(),
                    ))
                }
            }
        }
    }
}