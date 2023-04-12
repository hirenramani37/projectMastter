package com.massttr.provider.ui.main.myprofiles.editProfile.fragment

import android.os.Bundle
import android.view.View
import com.common.base.BaseFragment
import com.massttr.user.utils.EventBus
import com.massttr.user.utils.getTextString
import com.massttr.user.utils.isBlank
import com.massttr.user.utils.setShakeError
import com.massttr.provider.R
import com.massttr.provider.databinding.FragmentCompanyDetailsBinding
import com.massttr.provider.ui.language.login.register.verify.fixerRegistration.LocationActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import timber.log.Timber

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class CompanyDetailsFragment :
    BaseFragment<FragmentCompanyDetailsBinding>(R.layout.fragment_company_details),View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getBusEvent()
        onClickListener()

    }

    private fun initView() {
        binding.run {
            pref.userInfo?.let {
                etCompanyName.setText(it.company_name)
                etCVRNumber.setText(it.company_vat_no)
                etCompanyEmailAddress.setText(it.email_id)
                etZipCode.setText(it.company_zipcode)
                etCompanyAddress.setText(it.company_address)
            }
        }
    }

    private fun onClickListener(){
        binding.imgLocation.setOnClickListener(this)
    }

    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    private fun getBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey("UpdateProfile")) {
                //  Timber.e("UpdateProfile")
                val updateProfile = it.getBoolean("UpdateProfile", false)
                if (updateProfile) {
                    companyDetails()
                }
            }else if(it.containsKey("ADDRESS_CHANGE")){
                Timber.e("ADDRESS_CHANGE")
                val ADDRESS = it.getBoolean("ADDRESS_CHANGE", false)
                val aDDRESSEdit = it.getString("ADDRESS")
                val city = it.getString("city")
                val state = it.getString("state")

                if (ADDRESS) {
                    binding.etCompanyAddress.setText(aDDRESSEdit)
                }

            }
        }
    }

    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    private fun companyDetails(): Boolean {
        binding.run {
            when {
//                etCompanyName.isBlank() -> requireActivity().setShakeError(getString(R.string.please_enter_company_name))
//                etCVRNumber.isBlank() -> requireActivity().setShakeError(getString(R.string.please_enter_cvr))
//                etCompanyEmailAddress.isBlank() -> requireActivity().setShakeError(getString(R.string.please_enter_add))
//                etZipCode.isBlank() -> requireActivity().setShakeError(getString(R.string.please_enter_add))
//                etCompanyAddress.isBlank() -> requireActivity().setShakeError(getString(R.string.please_enter_add))
                else -> {
                    Timber.e("Company Details")
                    val bundle = Bundle()
                    bundle.putInt("Profile", 1)
                    EventBus.publish(bundle)
                    dataSave()
                    return true
                }
            }
            return false
        }
    }

    private fun dataSave() {
        binding.run {
            val userInfo = pref.userInfo
            userInfo?.company_name = etCompanyName.getTextString()
            userInfo?.company_vat_no = etCVRNumber.getTextString()
            userInfo?.email_id = etCompanyEmailAddress.getTextString()
            userInfo?.company_zipcode = etZipCode.getTextString()
            userInfo?.company_address = etCompanyAddress.getTextString()
            userInfo?.company = true
            pref.userInfo = userInfo
        }
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imgLocation->{
                requireActivity().startActivity<LocationActivity>()
            }
        }

    }
}