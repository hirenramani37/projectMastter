package com.massttr.provider.ui.main.myprofiles.editProfile.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.common.base.BaseFragment
import com.massttr.user.utils.*
import com.massttr.provider.R
import com.massttr.provider.databinding.FragmentPersonalDetailsBinding
import com.massttr.provider.ui.language.login.register.verify.fixerRegistration.LocationActivity
import com.massttr.provider.ui.main.myprofiles.editProfile.EditProfileActivity
import com.massttr.provider.ui.main.myprofiles.editProfile.EditProfileViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.util.*

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class PersonalDetailsFragment :
    BaseFragment<FragmentPersonalDetailsBinding>(R.layout.fragment_personal_details),
    View.OnClickListener {

    private val viewModel: EditProfileViewModel by viewModels()
    lateinit var activity: EditProfileActivity
    private var stateId: Int = 0
    private var citiesId: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBusEvent()
        init()
        setUpObserver()
        clickListener()
    }

    private fun clickListener() {
        binding.run {
            tvDateDOB.setOnClickListener(this@PersonalDetailsFragment)
            imgLocation.setOnClickListener(this@PersonalDetailsFragment)
        }
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(requireActivity()) { handleError(it) }
            appLoader.observe(requireActivity()) { updateLoaderUI(it) }
//            state.observe(requireActivity()) { state ->
//                binding.spState.setUpSpinnerCategory(state.map { it.en_name } as ArrayList<String>,
//                    "State")
//                pref.userInfo?.state?.toInt()?.let { binding.spState.setSelection(it, false) }
//                binding.spState.onItemSelectedListener =
//                    object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(
//                            parent: AdapterView<*>, view: View,
//                            pos: Int, id: Long,
//                        ) {
//                            stateId = pos + 1
//                            val userInfo = pref.userInfo
//                            userInfo?.state_name = parent.getItemAtPosition(pos).toString()
//                            userInfo?.state = stateId.toString()
//                            pref.userInfo = userInfo
//                            if (pos != 0) {
//                                val city = City(
//                                    stateId.toString(),
//                                    "test"
//                                )
//                                pref.userInfo?.city?.toInt()
//                                    ?.let { binding.spCity.setSelection(it, false) }
//                                viewModel.city(city)
//                            }
//                        }
//
//                        override fun onNothingSelected(arg0: AdapterView<*>?) {}
//                    }
//
//                city.observe(requireActivity()) { city ->
//                    binding.spCity.setUpSpinnerCategory(city.map { it.en_name } as ArrayList<String>,
//                        "City")
//
//                    binding.spCity.onItemSelectedListener =
//                        object : AdapterView.OnItemSelectedListener {
//                            override fun onItemSelected(
//                                parent: AdapterView<*>, view: View,
//                                pos: Int, id: Long,
//                            ) {
//                                citiesId = pos + 1
//                                val userInfo = pref.userInfo
//                                userInfo?.city_name = parent.getItemAtPosition(pos).toString()
//                                userInfo?.city = citiesId.toString()
//                                pref.userInfo = userInfo
//                            }
//
//                            override fun onNothingSelected(arg0: AdapterView<*>?) {}
//                        }
//                }
//            }
        }
    }

    private fun init() {
        viewModel.state()
        binding.run {
            pref.userInfo?.let {
                etFName.setText(it.full_name)
                etMno.setText(it.phone_no)
                etLEmail.setText(it.email_id)
                edZipCode.setText(it.zipcode)
                etAddress.setText(it.address)
                tvDateDOB.text = it.birth_date
            }
        }
    }

    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    private fun getBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey("UpdateProfile")) {
                //Timber.e("UpdateProfile")
                val updateProfile = it.getBoolean("UpdateProfile", false)
                if (updateProfile) {
                    personalDetails()
                }
            }else if(it.containsKey("ADDRESS_CHANGE")){
                Timber.e("ADDRESS_CHANGE")
                val ADDRESS = it.getBoolean("ADDRESS_CHANGE", false)
                val aDDRESSEdit = it.getString("ADDRESS")
                val city = it.getString("city")
                val state = it.getString("state")

                if (ADDRESS) {
                    binding.etAddress.setText(aDDRESSEdit)
                    binding.spCity.setText(city)
                    binding.spState.setText(state)
                }
            }
        }
    }





    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    private fun personalDetails(): Boolean {
        binding.run {
            when {
                etFName.isBlank() -> requireActivity().setShakeError(getString(R.string.please_enter_Fname))
                etMno.isBlank() -> requireActivity().setShakeError(getString(R.string.please_enter_number))
//                etLEmail.isBlank() -> requireActivity().setShakeError("Enter a Email Address")
                tvDateDOB.text.isBlank() -> requireActivity().setShakeError("Enter a DOB")
//                edZipCode.isBlank() -> requireActivity().setShakeError("Enter a Zipcode")
                etAddress.isBlank() -> requireActivity().setShakeError(getString(R.string.please_enter_address))
                else -> {
                    Timber.e("Personal Details")
                    val bundle = Bundle()
                    bundle.putInt("Profile", 0)
                    // bundle.putString("name",binding.etFName.text.toString().trim())
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
            userInfo?.first_name = etFName.getTextString()
            userInfo?.email_id = etLEmail.getTextString()
            userInfo?.phone_no = etMno.getTextString()
            userInfo?.country_code = 964
            userInfo?.zipcode = edZipCode.getTextString()
            userInfo?.address = etAddress.getTextString()
            userInfo?.personal = true
            userInfo?.gender = if (rbMale.isChecked) 1 else 0
            userInfo?.birth_date = tvDateDOB.text.toString()
            pref.userInfo = userInfo
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvDateDOB -> {
                val dpd = DatePickerDialog(
                    requireContext(),
                    { _, year, month, date ->
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
            R.id.imgLocation -> {
                requireActivity().startActivity<LocationActivity>()
            }
        }
    }
}