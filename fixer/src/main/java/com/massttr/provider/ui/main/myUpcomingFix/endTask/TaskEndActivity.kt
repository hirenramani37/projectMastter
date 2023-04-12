package com.massttr.provider.ui.main.myUpcomingFix.endTask

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityTaskEndBinding
import com.massttr.provider.ui.main.myUpcomingFix.startTasks.StartTasksActivity
import com.massttr.user.utils.JOB_END_ACTIVITY
import org.jetbrains.anko.startActivity


class TaskEndActivity : BaseActivity<ActivityTaskEndBinding>(R.layout.activity_task_end),
    View.OnClickListener {

    var taskIds = 0

    companion object {
        const val SUCCESSIVE_TYPE = "successive_type"
        const val REQ_SELECT_LANGUAGE = 0X102

        const val TASK_ID = "task_id"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initClickListener()
    }

    private fun initView() {
        binding.toolBar.tvTitle.text = getString(R.string.end_task)
        if (intent.hasExtra(TASK_ID)) {
            taskIds = intent.getIntExtra(TASK_ID, 0)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnYes -> {
                startActivity<StartTasksActivity>(JOB_END_ACTIVITY to taskIds)
                finish()
//                val taskId = intent.getIntExtra(TASK_ID, 0)
//                val intent = Intent()
//                intent.putExtra(TASK_ID, taskId)
//                setResult(Activity.RESULT_OK, intent)
//                finish()
            }
            R.id.btnNoo -> onBackPressed()
        }
    }

    private fun initClickListener() {
        binding.run {
            toolBar.imgBack.setOnClickListener(this@TaskEndActivity)
            btnYes.setOnClickListener(this@TaskEndActivity)
            btnNoo.setOnClickListener(this@TaskEndActivity)
        }
    }
}
