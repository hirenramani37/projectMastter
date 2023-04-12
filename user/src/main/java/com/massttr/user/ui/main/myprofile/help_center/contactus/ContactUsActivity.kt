package com.massttr.user.ui.main.myprofile.help_center.contactus

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.request.ContactUs
import com.common.data.network.model.request.SelectReason
import com.massttr.user.R
import com.massttr.user.databinding.ActivityContactUsBinding
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.util.*
import kotlin.collections.ArrayList

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class ContactUsActivity : BaseActivity<ActivityContactUsBinding>(R.layout.activity_contact_us),
    View.OnClickListener {
    private val viewModel: ContactUsActivityViewModel by viewModels()
    private var reasonList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppGlobal.setStatusBarGradiant(this)
        super.onCreate(savedInstanceState)
        initView()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@ContactUsActivity) { handleError(it) }
            appLoader.observe(this@ContactUsActivity) { updateLoaderUI(it) }
            selectReason.observe(this@ContactUsActivity) { it ->
                it.data.forEach {
                    if (pref.isLanguage == true) {
                        reasonList.add(it.en_reason)
                    } else {
                        reasonList.add(it.ar_reason)
                    }
                }
                binding.spSelectReason.setUpSpinner(
                    ArrayList(reasonList),
                    getString(R.string.select_reason)
                )
            }

            contactUs.observe(this@ContactUsActivity) {
                onBackPressed()
            }

            settings.observe(this@ContactUsActivity) {
                binding.tvEmailId.text = it.data?.contact_email
                binding.tvPhoneNumber.text = it.data?.contact_no
            }
        }
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@ContactUsActivity)
            tvEmailId.setOnClickListener(this@ContactUsActivity)
            tvPhoneNumber.setOnClickListener(this@ContactUsActivity)
            btnContactUs.setOnClickListener(this@ContactUsActivity)
        }
    }

    private fun initView() {
        binding.toolbar.tvTitle.text = getString(R.string.contact_us)
        viewModel.selectReason(SelectReason("1"))
        viewModel.settings()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.tvEmailId -> this.gmail(binding.tvEmailId.text.toString())
            R.id.tvPhoneNumber -> this.call(binding.tvPhoneNumber.text.toString())
            R.id.btnContactUs -> validate()
        }
    }

    private fun validate() {
        binding.run {
            when {
                spSelectReason.selectedItem.toString().trim() == getString(R.string.select_reason) -> setShakeError(
                    getString(R.string.select_reason)
                )
                etDescription.isBlank() -> setShakeError(getString(R.string.description))
                else ->
                    viewModel.contactUs(
                        ContactUs(
                            spSelectReason.selectedItemId.toString(),
                            etDescription.getTextString()
                        )
                    )
            }
        }
    }
}