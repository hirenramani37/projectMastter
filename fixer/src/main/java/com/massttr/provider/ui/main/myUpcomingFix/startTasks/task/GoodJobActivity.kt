package com.massttr.provider.ui.main.myUpcomingFix.startTasks.task

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.utils.EventBus
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityGoodJobBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class GoodJobActivity : BaseActivity<ActivityGoodJobBinding>(R.layout.activity_good_job),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListener()
        initView()
    }

    private fun initView() {
        binding.toolBar.tvTitle.text = getString(R.string.end_task)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack ->  onBackPressed()
            R.id.btnYes -> {
                val bundle = Bundle()
                bundle.putBoolean("END_TASK", true)
                EventBus.publish(bundle)
                finish()
            }
        }
    }

    private fun initClickListener() {
        binding.run {
            toolBar.imgBack.setOnClickListener(this@GoodJobActivity)
            btnYes.setOnClickListener(this@GoodJobActivity)
        }
    }
}
