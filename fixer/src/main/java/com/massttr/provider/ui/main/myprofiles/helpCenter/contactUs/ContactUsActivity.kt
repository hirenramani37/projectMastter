package com.massttr.provider.ui.main.myprofiles.helpCenter.contactUs

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.request.ContactUs
import com.common.data.network.model.request.SelectReason
import com.common.data.prefs.SharedPref
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityContactUsBinding
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class ContactUsActivity : BaseActivity<ActivityContactUsBinding>(R.layout.activity_contact_us),
    View.OnClickListener {
    private val viewModel: ContactUsViewModel by viewModels()
    private var reasonList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //AppGlobal.setStatusBarGradiant(this)
        super.onCreate(savedInstanceState)
        init()
        setObserver()
        clickListener()
    }


    private fun setObserver() {
        viewModel.run {
            appLoader.observe(this@ContactUsActivity) { updateLoaderUI(it) }
            apiErrors.observe(this@ContactUsActivity) { handleError(it) }
            contact.observe(this@ContactUsActivity) {
                setShakeSuccess(it.message)
                binding.etDescription.text.clear()
            }

            reason.observe(this@ContactUsActivity) {

                it.data.forEach { name ->
                    if(pref.isArabic==true){
                        reasonList.add(name.ar_reason)
                    }else{
                        reasonList.add(name.en_reason)
                    }

                }
                binding.spSelectReason.setUpSpinner(
                    ArrayList(reasonList),
                    getString(R.string.select_reason)
                )
            }

            settings.observe(this@ContactUsActivity) {
                binding.run {
                    tvPhoneNumber.text = it.data?.contact_no
                    tvEmailId.text = it.data?.fixer_support_email
                }
            }
        }
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@ContactUsActivity)
            btnSend.setOnClickListener(this@ContactUsActivity)
            tvEmailId.setOnClickListener(this@ContactUsActivity)
            tvPhoneNumber.setOnClickListener(this@ContactUsActivity)
        }
    }

    private fun init() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.contact_us)
            viewModel.settings()
            viewModel.selectReason(SelectReason("1"))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnSend -> validate()
            R.id.tvEmailId -> this.gmail(binding.tvEmailId.text.toString())
            R.id.tvPhoneNumber -> this.call(binding.tvPhoneNumber.text.toString())
        }
    }

    private fun validate() {
        binding.run {
            when {
                spSelectReason
                    .selectedItem
                    .toString()
                    .trim() == getString(R.string.select_reason) ->
                    setShakeError(getString(R.string.select_reason))
                etDescription.isBlank() -> setShakeError(getString(R.string.description))
                else -> viewModel.contactUs(
                        ContactUs(
                            spSelectReason.selectedItemId.toString(),
                            spSelectReason.selectedItem.toString(),
                            etDescription.getTextString()
                        )
                    )
            }
        }
    }
}