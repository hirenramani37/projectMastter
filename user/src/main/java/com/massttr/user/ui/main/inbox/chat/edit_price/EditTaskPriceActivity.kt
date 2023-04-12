package com.massttr.user.ui.main.inbox.chat.edit_price

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.request.ChangePrice
import com.massttr.user.R
import com.massttr.user.databinding.ActivityTaskPriceBinding
import com.massttr.user.utils.getTextString
import com.massttr.user.utils.isBlank
import com.massttr.user.utils.setShakeError
import com.massttr.user.utils.visible
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class EditTaskPriceActivity : BaseActivity<ActivityTaskPriceBinding>(R.layout.activity_task_price),
    View.OnClickListener {

    private val viewModel: EditTaskPriceActivityViewModel by viewModels()
    private var jobID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpUI()
        setUpObserver()
        clickListener()
    }

    companion object {
        const val PRICE = "PRICE"
        const val JOB_ID = "JOB_ID"
        const val PRICE_TYPE = "PRICE_TYPE"
        const val RESULT_CHANGED_PRICE = "RESULT_CHANGED_PRICE"
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@EditTaskPriceActivity) { handleError(it) }
            appLoader.observe(this@EditTaskPriceActivity) { updateLoaderUI(it) }
            changePrice.observe(this@EditTaskPriceActivity) {
                val intent = Intent()
                intent.putExtra(RESULT_CHANGED_PRICE, binding.etPrice.getTextString())
                intent.putExtra(PRICE, binding.etPrice.getTextString())
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setUpUI() {
        binding.run {
            toolBar.tvTitle.text = getString(R.string.task_price)
            if (intent.hasExtra(PRICE)) etPrice.setText(intent.getStringExtra(PRICE))
            if (intent.hasExtra(JOB_ID)) jobID = intent.getIntExtra(JOB_ID, 0)
            if (intent.hasExtra(PRICE_TYPE))
                if (intent.getIntExtra(PRICE_TYPE, 0) == 1)
                    etHourlyRate.visible()
        }
    }

    private fun clickListener() {
        binding.run {
            btnSubmit.setOnClickListener(this@EditTaskPriceActivity)
            toolBar.imgBack.setOnClickListener(this@EditTaskPriceActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSubmit -> validation()
            R.id.imgBack -> onBackPressed()
        }
    }

    private fun validation() {
        binding.run {
            when {
                etPrice.isBlank() -> etPrice.setShakeError(getString(R.string.enter_task_price))
                else ->
                    viewModel.changePrice(
                        ChangePrice(
                            etPrice.getTextString(),
                            jobID,
                            if (etHourlyRate.getTextString().isBlank()) "" else etHourlyRate.getTextString()
                        )
                    )
            }
        }
    }
}