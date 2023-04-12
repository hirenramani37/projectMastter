package com.massttr.provider.ui.main.myprofiles

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.CompoundButton
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import coil.load
import com.common.base.BaseFragment
import com.common.data.network.model.request.Language
import com.common.data.network.model.request.StatusCheck
import com.common.multilanguage.LocaleManager
import com.massttr.provider.R
import com.massttr.provider.databinding.DialogLogoutBinding
import com.massttr.provider.databinding.FragmentProfileBinding
import com.massttr.provider.ui.language.login.LoginActivity
import com.massttr.provider.ui.main.myprofiles.completedTasks.CompletedTasksActivity
import com.massttr.provider.ui.main.myprofiles.deleteAccount.DeleteAccountActivity
import com.massttr.provider.ui.main.myprofiles.earning.EarningActivity
import com.massttr.provider.ui.main.myprofiles.editProfile.EditProfileActivity
import com.massttr.provider.ui.main.myprofiles.helpCenter.HelpCenterActivity
import com.massttr.provider.ui.main.myprofiles.inbox.InboxActivity
import com.massttr.provider.ui.main.myprofiles.manages.ManageActivity
import com.massttr.user.utils.BUS_EVENT_CHAT_COUNT
import com.massttr.user.utils.EventBus
import com.massttr.user.utils.setShakeSuccess
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.*
import java.util.*

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile),
    View.OnClickListener {

    private val viewModel: ProfileFragmentViewModel by viewModels()
    private var isArabic = false
    private var chatCount: Int = 0

    companion object {
        private const val REQ_EDIT_PROFILE = 100
        private const val TIME_DELAY = 2000
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setUpObserver()
        updateUi()
        initBusEvent()
        countUpdate(pref.chatCount ?: 0)
        clickListener()
    }

    private fun updateUi() {
        if (pref.userInfo?.is_online == 1) {
          //  binding.lbSwitch.setOnCheckedChangeListener(null)
            binding.lbSwitch.isOn = true
           // binding.lbSwitch.setOnCheckedChangeListener(this)

           // binding.lbSwitch.setText(R.string.online)
        } else {
           // binding.lbSwitch.setOnCheckedChangeListener(null)
            binding.lbSwitch.isOn = false
          //  binding.lbSwitch.setOnCheckedChangeListener(this)
          //  lblOnlineOffline.setText(R.string.offline)
        }

    }

    private fun init() {
        binding.run {
            // lbSwitch.isChecked = pref.isOnline == true
            binding.lbSwitch.labelOn
            pref.userInfo.let {
                tvProfileName.text = it?.full_name
                civProfile.load(it?.profile_picture) {
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
            }
            if (LocaleManager.isEnglish(requireContext()))
                ivFlagLanguage.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_english_language_round_flag,
                    0,
                    R.drawable.ic_fill_down_arrow,
                    0
                )
            else
                ivFlagLanguage.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_iraq_language_roud_flag,
                    0,
                    R.drawable.ic_fill_down_arrow,
                    0
                )
        }

        binding.lbSwitch.setOnToggledListener { toggleableView, isOn ->
           if(isOn){
               viewModel.statusCheck(StatusCheck(1))
               binding.lbSwitch.isOn = isOn
           }else{
               viewModel.statusCheck(StatusCheck(0))
               binding.lbSwitch.isOn = isOn
           }
        }



    }

    private fun countUpdate(chatCount: Int) {
        if (chatCount == 0) {
            binding.tvInboxCount.isVisible = false
        } else {
            binding.tvInboxCount.isVisible = true
            binding.tvInboxCount.text = chatCount.toString()
        }
    }

    private fun initBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey(BUS_EVENT_CHAT_COUNT)) {
                val readCountChat = it.getInt(BUS_EVENT_CHAT_COUNT, 0)
                pref.chatCount = readCountChat
                chatCount = readCountChat
                countUpdate(readCountChat)
            }
        }
    }

    private fun setUpObserver() {
        viewModel.run {
            appLoader.observe(requireActivity()) { updateLoaderUI(it) }
            apiErrors.observe(requireActivity()) { handleError(it) }
            logout.observe(requireActivity()) {
                pref.clearAppUserData()
                startActivity(
                    requireActivity().intentFor<LoginActivity>().newTask().clearTask()
                )
            }
            language.observe(requireActivity()) {
                //pref.isArabic = isArabic
                requireActivity().setShakeSuccess(it.message)
            }
            status.observe(requireActivity()) {
                val userInfo = pref.userInfo
                userInfo?.is_online = it.data?.is_online ?: 0
                pref.userInfo = userInfo
                requireActivity().setShakeSuccess(it.message)
            }
        }
    }

    private fun clickListener() {
        binding.run {
            llInbox.setOnClickListener(this@ProfileFragment)
            llCompleteTasks.setOnClickListener(this@ProfileFragment)
            llManage.setOnClickListener(this@ProfileFragment)
            llHelpCenter.setOnClickListener(this@ProfileFragment)
            llEarning.setOnClickListener(this@ProfileFragment)
            llLogout.setOnClickListener(this@ProfileFragment)
            ivEditProfile.setOnClickListener(this@ProfileFragment)
            ivFlagLanguage.setOnClickListener(this@ProfileFragment)
            btnDeleteAccount.setOnClickListener(this@ProfileFragment)
            lbSwitch.setOnClickListener(this@ProfileFragment)
        }
    }

    override fun onClick(it: View?) {
        when (it?.id) {
            R.id.llInbox -> requireActivity().startActivity<InboxActivity>()
            R.id.llCompleteTasks -> requireActivity().startActivity<CompletedTasksActivity>()
            R.id.llManage -> requireActivity().startActivity<ManageActivity>()
            R.id.llEarning -> requireActivity().startActivity<EarningActivity>()
            R.id.llHelpCenter -> requireActivity().startActivity<HelpCenterActivity>()
            R.id.btnDeleteAccount -> requireActivity().startActivity<DeleteAccountActivity>()
            R.id.llLogout -> logout()
            R.id.ivFlagLanguage -> popUpLanguage()
            R.id.ivEditProfile -> requireActivity().startActivityForResult<EditProfileActivity>(
                REQ_EDIT_PROFILE
            )
        }
    }

    private fun popUpLanguage() {
        val lp: WindowManager.LayoutParams = requireActivity().window!!.attributes
        requireActivity().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        lp.alpha = 0.2f
        requireActivity().window?.attributes = lp
        val popup = PopupMenu(requireActivity(), binding.ivFlagLanguage)
        popup.setForceShowIcon(true)
        popup.inflate(R.menu.message_popup_menu_item)
        popup.gravity = Gravity.END
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuIraq -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.ivFlagLanguage.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_iraq_language_roud_flag,
                            0,
                            R.drawable.ic_down_arrow,
                            0
                        )
                    }, 2000)
                    val bundle = Bundle()
                    bundle.putBoolean("Language", true)
                    EventBus.publish(bundle)
                    isArabic = true
                    pref.isArabic = true
                    pref.selectLanguage = "ar"
                    viewModel.language(Language("ar"))
                    popup.dismiss()
                }
                R.id.menuEnglish -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.ivFlagLanguage.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_english_language_round_flag,
                            0,
                            R.drawable.ic_down_arrow,
                            0
                        )
                    }, 2000)
                    val bundle = Bundle()
                    bundle.putBoolean("Language", false)
                    EventBus.publish(bundle)
                    isArabic = false
                    pref.isArabic = false
                    pref.selectLanguage = "en"
                    viewModel.language(Language("en"))
                    popup.dismiss()
                }
            }
            false
        }
        popup.setOnDismissListener {
            val rightSide: WindowManager.LayoutParams = requireActivity().window!!.attributes
            rightSide.alpha = 1f
            requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            requireActivity().window?.attributes = rightSide
        }
        //popup.menu.findItem(R.id.menuIraq).setIcon(R.drawable.ic_iraq_language_roud_flag)
        popup.show()
    }

    private fun logout() {
        val dialog = Dialog(requireActivity())
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window?.setDimAmount(0.80f)

        val binding: DialogLogoutBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireActivity()),
                R.layout.dialog_logout,
                null,
                false
            )

        dialog.setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            dialog.dismiss()
        }
        binding.tvCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_EDIT_PROFILE -> pref.userInfo.let {
                    binding.tvProfileName.text = it?.full_name
                    binding.civProfile.load(it?.profile_picture)
                }
            }
        }
    }




//    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//        if (isChecked) {
//            binding.lbSwitch.isChecked = isChecked
//
//            viewModel.statusCheck(StatusCheck(1))
//        } else {
//            binding.lbSwitch.isChecked = isChecked
//
//            viewModel.statusCheck(StatusCheck(0))
//        }
//    }
}

