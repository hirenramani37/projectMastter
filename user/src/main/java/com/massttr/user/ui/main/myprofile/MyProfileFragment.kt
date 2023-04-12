package com.massttr.user.ui.main.myprofile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.common.base.BaseFragment
import com.common.data.network.model.request.Language
import com.common.multilanguage.LocaleManager
import com.massttr.user.BuildConfig
import com.massttr.user.R
import com.massttr.user.databinding.DialogLogoutBinding
import com.massttr.user.databinding.FragmentProfileBinding
import com.massttr.user.ui.language.login.LoginActivity
import com.massttr.user.ui.main.myprofile.delete_account.DeleteAccountActivity
import com.massttr.user.ui.main.myprofile.edit_profile.EditProfileActivity
import com.massttr.user.ui.main.myprofile.help_center.HelpCenterActivity
import com.massttr.user.ui.main.myprofile.order_history.OrderHistoryActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.util.*

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class MyProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile),
    View.OnClickListener {

    private val viewModel: MyProfileViewModel by viewModels()
    private val popupMenuTime = 2000

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setUpLang()
        setUpObserver()
        clickListener()
    }

    private fun initView() {
        binding.run {
            pref.userInfo.let {
                tvProfileName.text = it?.full_name
                it?.profile_picture?.let { image ->
                    requireActivity().loadImages(
                        image,
                        binding.civProfile, R.drawable.ic_placeholder
                    )
                }
            }
        }
    }

    private fun setUpObserver() {
        viewModel.run {
            appLoader.observe(requireActivity()) { updateLoaderUI(it) }
            apiErrors.observe(requireActivity()) { handleError(it) }
            logout.observe(requireActivity()) {
                pref.clearAppUserData()
                startActivity(requireActivity().intentFor<LoginActivity>().newTask().clearTask())
            }
            language.observe(requireActivity()) {
                if (it.success) {
                    if (it.message == "Language change successfully") {
                        pref.isLanguage = true
                        it.message.showSuccessToast()
                    } else {
                        pref.isLanguage = false
                        it.message.showSuccessToast()
                    }
                } else {
                    it.message.showErrorToast()
                }
            }
        }
    }

    private fun setUpLang() {
        if (LocaleManager.isEnglish(requireContext())) {
            binding.ivFlagLanguage.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_english_language_round_flag,
                0,
                R.drawable.ic_down_arrow,
                0
            )
        } else {
            binding.ivFlagLanguage.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_iraq_language_roud_flag,
                0,
                R.drawable.ic_down_arrow,
                0
            )
        }
    }

    private fun clickListener() {
        binding.run {
            llOrderHistory.setOnClickListener(this@MyProfileFragment)
            llHelpCenter.setOnClickListener(this@MyProfileFragment)
            llLogout.setOnClickListener(this@MyProfileFragment)
            btnDeleteAccount.setOnClickListener(this@MyProfileFragment)
            llInviteFriends.setOnClickListener(this@MyProfileFragment)
            ivFlagLanguage.setOnClickListener(this@MyProfileFragment)
            ivEditProfile.setOnClickListener(this@MyProfileFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llOrderHistory -> requireActivity().startActivity<OrderHistoryActivity>()
            R.id.llInviteFriends -> requireActivity().invite("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n")
            R.id.llHelpCenter -> requireActivity().startActivity<HelpCenterActivity>()
            R.id.ivEditProfile -> requireActivity().startActivity<EditProfileActivity>()
            R.id.btnDeleteAccount -> requireActivity().startActivity<DeleteAccountActivity>()
            R.id.llLogout -> logoutAlert()
            R.id.ivFlagLanguage -> popUpLanguage()
        }
    }

    private fun popUpLanguage() {
        val lp: WindowManager.LayoutParams = requireActivity().window!!.attributes
        requireActivity().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requireActivity().window?.attributes = lp
        lp.alpha = 0.2f
        val popup = PopupMenu(requireActivity(), binding.ivFlagLanguage)
        popup.setForceShowIcon(true)
        popup.inflate(R.menu.message_popup_menu_item)
        popup.gravity = Gravity.END
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuIraq -> {
                    viewModel.language(Language("ar"))
                    val bundle = Bundle()
                    bundle.putBoolean("Language", true)
                    EventBus.publish(bundle)
                    popup.dismiss()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.ivFlagLanguage.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_iraq_language_roud_flag,
                            0,
                            R.drawable.ic_down_arrow,
                            0
                        )
                    }, popupMenuTime.toLong())
                }
                R.id.menuEnglish -> {
                    viewModel.language(Language("en"))
                    val bundle = Bundle()
                    bundle.putBoolean("Language", false)
                    EventBus.publish(bundle)
                    popup.dismiss()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.ivFlagLanguage.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_english_language_round_flag,
                            0,
                            R.drawable.ic_down_arrow,
                            0
                        )
                    }, popupMenuTime.toLong())
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
        popup.show()
    }

    private fun logoutAlert() {
        val dialog = Dialog(requireActivity())
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
}