package com.massttr.user.ui.main.myprofile.order_history.order_details.cancel_task.cancel_task_success

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityTaskCompleteDoneBinding

class TaskCompleteDoneActivity :
    BaseActivity<ActivityTaskCompleteDoneBinding>(R.layout.activity_task_complete_done),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
    }

    private fun initView() {
        binding.toolbar.tvTitle.text = getString(R.string.cancel_task)
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@TaskCompleteDoneActivity)
            btnGoToHome.setOnClickListener(this@TaskCompleteDoneActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnGoToHome -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        binding.btnGoToHome.performClick()
    }
}