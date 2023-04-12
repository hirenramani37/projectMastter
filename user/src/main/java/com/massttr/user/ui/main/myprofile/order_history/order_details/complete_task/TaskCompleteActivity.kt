package com.massttr.user.ui.main.myprofile.order_history.order_details.complete_task

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityTaskCompleteBinding
import com.massttr.user.ui.main.myprofile.order_history.OrderHistoryActivity
import org.jetbrains.anko.startActivity

class TaskCompleteActivity :
    BaseActivity<ActivityTaskCompleteBinding>(R.layout.activity_task_complete),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
    }

    private fun initView() {
        binding.toolbar.tvTitle.text = getString(R.string.task_complete)
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@TaskCompleteActivity)
            btnGoToHistory.setOnClickListener(this@TaskCompleteActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnGoToHistory -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        binding.btnGoToHistory.performClick()
        super.onBackPressed()
    }
}