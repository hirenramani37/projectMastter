package com.massttr.provider.ui.main.myprofiles.completedTasks.viewReceipt.receiptSuccess

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.utils.EventBus
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityViewReceiptSuccessBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class ViewReceiptSuccessActivity :
    BaseActivity<ActivityViewReceiptSuccessBinding>(R.layout.activity_view_receipt_success),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@ViewReceiptSuccessActivity)
            btnMyUpComing.setOnClickListener(this@ViewReceiptSuccessActivity)
        }
    }

    private fun initView() {
        binding.toolbar.tvTitle.text = getString(R.string.view_receipt)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnMyUpComing -> {
                val bundle = Bundle()
                bundle.putBoolean("ViewReceipt", true)
                EventBus.publish(bundle)
               // startActivity<MainActivity>("ViewReceipt" to true)
                onBackPressed()
                //finish()
               // finishAffinity()
            }
        }
    }
}