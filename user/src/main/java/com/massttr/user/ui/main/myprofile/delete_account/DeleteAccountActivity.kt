package com.massttr.user.ui.main.myprofile.delete_account

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityDeleteAccountBinding
import com.massttr.user.ui.language.login.LoginActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class DeleteAccountActivity :
    BaseActivity<ActivityDeleteAccountBinding>(R.layout.activity_delete_account),
    View.OnClickListener {

    private val viewModel: DeleteAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setObserver()
        clickListener()
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@DeleteAccountActivity)
            btnYes.setOnClickListener(this@DeleteAccountActivity)
            btnNoo.setOnClickListener(this@DeleteAccountActivity)
        }
    }

    private fun setObserver() {
        viewModel.run {
            appLoader.observe(this@DeleteAccountActivity) { updateLoaderUI(it) }
            apiErrors.observe(this@DeleteAccountActivity) { handleError(it) }
            delete.observe(this@DeleteAccountActivity) {
                pref.clearAppUserData()
                startActivity(intentFor<LoginActivity>().newTask().clearTask())
            }
        }
    }

    private fun initView() {
        binding.toolbar.tvTitle.text = getString(R.string.delete_account)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnNoo -> onBackPressed()
            R.id.btnYes -> viewModel.delete()
        }
    }
}