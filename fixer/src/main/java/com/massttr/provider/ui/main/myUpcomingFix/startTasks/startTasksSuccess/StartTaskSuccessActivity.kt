package com.massttr.provider.ui.main.myUpcomingFix.startTasks.startTasksSuccess

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.utils.EventBus
import com.massttr.user.utils.JOB_ACCEPT_ACTIVITY
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityStartTaskSuccessBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class StartTaskSuccessActivity :
    BaseActivity<ActivityStartTaskSuccessBinding>(R.layout.activity_start_task_success),
    View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
    }


    private fun initView() {
        binding.run {
            toolBar.tvTitle.text = getString(R.string.start_task)
        }

        if (intent.hasExtra(JOB_ACCEPT_ACTIVITY)) {
            // Launch My Fix

        }
    }

    private fun clickListener() {
        binding.run {
            toolBar.imgBack.setOnClickListener(this@StartTaskSuccessActivity)
            btnMyUpComing.setOnClickListener(this@StartTaskSuccessActivity)
        }
    }

    private fun jobAcceptAndConfirmed() {
        val bundle = Bundle()
        bundle.putBoolean(JOB_ACCEPT_ACTIVITY, true)
        EventBus.publish(bundle)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnMyUpComing -> {
                jobAcceptAndConfirmed()
                finish()
            }
        }
    }
}
