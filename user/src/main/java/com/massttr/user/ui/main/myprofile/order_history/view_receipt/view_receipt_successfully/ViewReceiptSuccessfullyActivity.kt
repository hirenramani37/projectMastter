package com.massttr.user.ui.main.myprofile.order_history.view_receipt.view_receipt_successfully

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityViewReceiptSuccessfullyBinding
import com.massttr.user.ui.main.myprofile.order_history.OrderHistoryActivity
import org.jetbrains.anko.startActivity

class ViewReceiptSuccessfullyActivity :
    BaseActivity<ActivityViewReceiptSuccessfullyBinding>(R.layout.activity_view_receipt_successfully),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.view_receipt)
            btnOrderHistory.setOnClickListener(this@ViewReceiptSuccessfullyActivity)
            toolbar.imgBack.setOnClickListener(this@ViewReceiptSuccessfullyActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnOrderHistory -> startActivity<OrderHistoryActivity>()
        }
    }
}