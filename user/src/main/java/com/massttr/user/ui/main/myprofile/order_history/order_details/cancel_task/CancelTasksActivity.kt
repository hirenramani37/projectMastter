package com.massttr.user.ui.main.myprofile.order_history.order_details.cancel_task

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
import com.massttr.user.R
import com.massttr.user.databinding.ActivityCancelTasksBinding
import com.massttr.user.databinding.CancelTaskDialogBinding
import com.massttr.user.ui.main.myprofile.order_history.order_details.cancel_task.cancel_task_success.TaskCompleteDoneActivity
import com.massttr.user.utils.getTextString
import com.massttr.user.utils.isBlank
import com.massttr.user.utils.setShakeError
import com.massttr.user.utils.setUpSpinner
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivityForResult

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class CancelTasksActivity :
    BaseActivity<ActivityCancelTasksBinding>(R.layout.activity_cancel_tasks), View.OnClickListener {

    private val viewModel: CancelTaskActivityViewModel by viewModels()
    private lateinit var bindings: CancelTaskDialogBinding
    private var reasonList = ArrayList<String>()
    private var dialog: Dialog? = null
    private var jobId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setUpObserver()
        clickListener()
    }

    companion object {
        const val JOB_ID = "JOB_ID"
        const val REQ_CANCEL_COMPLETE = 78
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.cancel_task)
            viewModel.selectReason(SelectReason("0"))

            if (intent.hasExtra(JOB_ID)) jobId = intent.getIntExtra(JOB_ID, 0)
        }
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@CancelTasksActivity)
            btnYes.setOnClickListener(this@CancelTasksActivity)
            btnNoo.setOnClickListener(this@CancelTasksActivity)
        }
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@CancelTasksActivity) { handleError(it) }
            appLoader.observe(this@CancelTasksActivity) { updateLoaderUI(it) }
            selectReason.observe(this@CancelTasksActivity) { it ->
                it.data.forEach {
                    reasonList.add(it.en_reason)
                }
            }

            jobStatus.observe(this@CancelTasksActivity) {
                startActivityForResult<TaskCompleteDoneActivity>(REQ_CANCEL_COMPLETE)
            }
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
            LayoutInflater.from(this),
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
            imgDismiss.setOnClickListener {
                dialog!!.dismiss()
            }
            btnSend.setOnClickListener {
                when {
                    spSelectReason.selectedItem.toString()
                        .trim() == getString(R.string.select_reason) -> spSelectReason.setShakeError(
                        getString(R.string.select_reason)
                    )
                    etDescription.isBlank() -> etDescription.setShakeError(getString(R.string.description))
                    else -> {
                        viewModel.jobStatus(
                            JobChangeStatus(
                                jobId.toString(),
                                "6",
                                spSelectReason.selectedItemId.toString(),
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

    /* ArrayAdapter.createFromResource(
           this,
           R.array.cancel_reason,
           android.R.layout.select_dialog_item,
       ).also { adapter ->
           adapter.setDropDownViewResource(R.layout.custom_spinner)
           binding.spSelectReason.adapter = adapter
       }

       binding.spSelectReason.onItemSelectedListener = object :
           AdapterView.OnItemSelectedListener {
           override fun onItemSelected(
               parent: AdapterView<*>,
               view: View, position: Int, id: Long,
           ) {
               if (spSelect) {
                   (parent.getChildAt(0) as TextView).typeface =
                       ResourcesCompat.getFont(this@CancelTasksActivity, R.font.pangram_medium)
                   (parent.getChildAt(0) as TextView).setTextColor(
                       ContextCompat.getColor(
                           this@CancelTasksActivity,
                           R.color.colorBlackHelp
                       )
                   )
                   (parent.getChildAt(0) as TextView).textSize = 16F
               } else {
                   spSelect = true
                   (parent.getChildAt(0) as TextView).typeface =
                       ResourcesCompat.getFont(this@CancelTasksActivity, R.font.pangram_medium)
                   (parent.getChildAt(0) as TextView).setTextColor(
                       ContextCompat.getColor(
                           this@CancelTasksActivity,
                           R.color.tex_grayTwo
                       )
                   )
                   (parent.getChildAt(0) as TextView).textSize = 16F
               }
           }

           override fun onNothingSelected(parent: AdapterView<*>) {}
       }*/

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