package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.book_fixer

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityOrderAcceptedBinding
import com.massttr.user.ui.main.myprofile.order_history.OrderHistoryActivity
import org.jetbrains.anko.startActivity
class OrderAcceptedActivity :
    BaseActivity<ActivityOrderAcceptedBinding>(R.layout.activity_order_accepted),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
    }

    private fun initView() {
        binding.run {
            toolBar.tvTitle.text = getString(R.string.order_accepted)
        }
    }

    private fun clickListener() {
        binding.run {
            toolBar.imgBack.setOnClickListener(this@OrderAcceptedActivity)
            btnOrderHistory.setOnClickListener(this@OrderAcceptedActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnOrderHistory -> {
                startActivity<OrderHistoryActivity>()
                finish()
            }
        }
    }

    override fun onBackPressed() {
        binding.btnOrderHistory.performClick()
        super.onBackPressed()
    }
}