package com.massttr.provider.ui.main.myUpcomingFix.cancelTask

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.common.base.BaseActivity
import com.common.data.network.model.request.JobChangeStatus
import com.common.data.network.model.request.SelectReason
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityCancelTasksBinding
import com.massttr.provider.databinding.CancelTaskDialogBinding
import com.massttr.provider.ui.main.myUpcomingFix.cancelTask.cancelTasksDone.TaskCompleteDoneActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivityForResult
import timber.log.Timber


class CancelTasksActivity :
    BaseActivity<ActivityCancelTasksBinding>(R.layout.activity_cancel_tasks), View.OnClickListener {

    private val viewModel: CancelTasksActivityViewModel by viewModels()
    private lateinit var bindings: CancelTaskDialogBinding
    private var reasonList = ArrayList<String>()
    private var dialog: Dialog? = null
    private var jobId = 0

    companion object {
        const val REQ_CANCEL_COMPLETE = 77
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setUpObserver()
        clickListener()
    }

    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@CancelTasksActivity) { handleError(it) }
            appLoader.observe(this@CancelTasksActivity) { updateLoaderUI(it) }
            selectReason.observe(this@CancelTasksActivity) { it ->
                it.data.forEach {
                    Timber.e("ISArabic...${pref.isArabic}")
//                    reasonList.add(it.ar_reason)
                    if (pref.isArabic == true) {
                        reasonList.add(it.ar_reason)
                    } else {
                        reasonList.add(it.en_reason)
                    }
                }
            }
            changeJobStatus.observe(this@CancelTasksActivity) {
                startActivityForResult<TaskCompleteDoneActivity>(REQ_CANCEL_COMPLETE)
            }
        }
    }

    private fun clickListener() {
        binding.run {
            btnYes.setOnClickListener(this@CancelTasksActivity)
            btnNoo.setOnClickListener(this@CancelTasksActivity)
            toolbar.imgBack.setOnClickListener(this@CancelTasksActivity)
        }
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.cancle_task)
            if (intent.hasExtra(JOB_CANCEL_ACTIVITY)) jobId =
                intent.getIntExtra(JOB_CANCEL_ACTIVITY, 0)

            viewModel.selectReason(SelectReason("0"))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnYes -> cancelTask()
            R.id.btnNoo -> onBackPressed()
        }
    }

    private fun cancelTask() {
        dialog = Dialog(this)
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setDimAmount(0.80f)

        bindings = DataBindingUtil.inflate(
            LayoutInflater.from(this@CancelTasksActivity),
            R.layout.cancel_task_dialog,
            null,
            false
        )
        dialog!!.setContentView(bindings.root)

        bindings.run {
            spSelectReason.setUpSpinner(
                ArrayList(reasonList),
                getString(R.string.cancel)
            )
            imgDismiss.setOnClickListener { dialog!!.dismiss() }
            btnSend.setOnClickListener {
                when {
                    spSelectReason.selectedItem.toString()
                        .trim() == getString(R.string.select_reason) ->
                        setShakeError(getString(R.string.select_reason))
                    etDescription.isBlank() -> setShakeError(getString(R.string.description))
                    else -> {
                        viewModel.cancelStatus(
                            JobChangeStatus(
                                jobId,
                                7,
                                spSelectReason.selectedItemId.toInt(),
                                etDescription.getTextString()
                            )
                        )
                    }
                }
                dialog!!.dismiss()
            }
        }
        dialog!!.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_CANCEL_COMPLETE -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }
}