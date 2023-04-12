package com.massttr.provider.ui.main.myUpcomingFix.cancelTask.cancelTasksDone

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityTaskCompleteDoneBinding

class TaskCompleteDoneActivity :
    BaseActivity<ActivityTaskCompleteDoneBinding>(R.layout.activity_task_complete_done),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpUI()
        clickListener()
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@TaskCompleteDoneActivity)
            btnGoToHome.setOnClickListener(this@TaskCompleteDoneActivity)
        }
    }

    private fun setUpUI() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.cancle_task)
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