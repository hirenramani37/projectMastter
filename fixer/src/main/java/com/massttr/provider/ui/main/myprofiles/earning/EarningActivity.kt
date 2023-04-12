package com.massttr.provider.ui.main.myprofiles.earning

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.common.base.BaseActivity
import com.common.data.network.model.request.FixerEarning
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityEarningBinding
import com.massttr.provider.databinding.EarningDialogBinding
import com.massttr.user.utils.AppGlobal.Companion.convertToEnglish
import com.massttr.user.utils.getSystemDateCalendarFromUTCDate
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class EarningActivity : BaseActivity<ActivityEarningBinding>(R.layout.activity_earning),
    View.OnClickListener {

    private var createdAt: Calendar? = null
    private val viewModel: EarningViewModel by viewModels()
    private lateinit var earningAdapter: EarningAdapter
    private var earnings: String = " "
    private var commission: Double? = null
    private var totalEarning: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setObserver()
        clickListener()
    }

    private fun setObserver() {
        viewModel.run {
            appLoader.observe(this@EarningActivity) { updateLoaderUI(it) }
            apiErrors.observe(this@EarningActivity) { handleError(it) }
            fixerEarning.observe(this@EarningActivity) {
                it.data?.earning_list?.let { list ->
                    earningAdapter.addAll(list)
                }
                binding.tvPrice.text = String.format("%.2f", it.data?.total_earning).plus("QD")
                val earningsModels = it.data
                earnings = earningsModels?.earning.toString()
                commission = earningsModels?.total_admin_commission
                totalEarning = earningsModels?.total_earning
                binding.tvNotFound.isVisible = earningAdapter.displayList.size == 0
            }
        }
    }

    private fun init() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.earnings)
            earningAdapter = EarningAdapter()
            rvEarningList.adapter = earningAdapter
        }

        pref.accountCreatedDate?.let {
            createdAt = it.getSystemDateCalendarFromUTCDate()
            initBunchDate()
        }
    }

    private fun clickListener() {
        binding.run {
            tvTotalEarning.setOnClickListener(this@EarningActivity)
            toolbar.imgBack.setOnClickListener(this@EarningActivity)
            btnNext.setOnClickListener(this@EarningActivity)
            btnPrevious.setOnClickListener(this@EarningActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvTotalEarning -> alert(binding.tvPayOutDate.text.toString())
            R.id.imgBack -> onBackPressed()
            R.id.btnNext -> {
                createdAt?.add(Calendar.DATE, 15)
                initBunchDate()
            }
            R.id.btnPrevious -> {
                createdAt?.add(Calendar.DATE, -15)
                initBunchDate()
            }
        }
    }

    private fun allDateSetOn(formatDateStart: String, lastDayMonth: String) {
        binding.run {
            Timber.e("date: $formatDateStart  $lastDayMonth")
            tvStart.text = ("$formatDateStart - ")
            tvEnd.text = lastDayMonth
            tvDateTime.text = ("$formatDateStart - $lastDayMonth")
            tvPayOutDate.text = ("$formatDateStart - $lastDayMonth")
        }
    }


    private fun alert(fullDateEarnings: String) {
        val dialog = Dialog(this@EarningActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setDimAmount(0.80f)
        val binding: EarningDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this@EarningActivity),
            R.layout.earning_dialog,
            null,
            false
        )
        binding.run {
            tvPayout.text = earnings.plus("QD")
            tvPayoutCommission.text = String.format("%.2f", commission).plus("QD")
            tvTotal.text = String.format("%.2f", totalEarning).plus("QD")
            tvTitle.text = fullDateEarnings
            imgDismiss.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setContentView(binding.root)
        dialog.show()
    }

    private fun initBunchDate() {
        val leftDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
        val rightDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        createdAt?.let {
            if (it.get(Calendar.DAY_OF_MONTH) < 16) {
//            it will goes to 1 to 15 bunch
                val leftDateCalendar = Calendar.getInstance()
                leftDateCalendar.time = it.time
                leftDateCalendar.set(Calendar.DAY_OF_MONTH, 1)

                val rightDateCalendar = Calendar.getInstance()
                rightDateCalendar.time = it.time
                rightDateCalendar.set(Calendar.DAY_OF_MONTH, 15)

                allDateSetOn(
                    leftDateFormat.format(leftDateCalendar.time),
                    rightDateFormat.format(rightDateCalendar.time)
                )

                val startDay =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.ENGLISH
                    ).format(leftDateCalendar.time)
                        .toString()
                val endDay =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.ENGLISH
                    ).format(rightDateCalendar.time)
                        .toString()
                viewModel.fixerEarning(
                    FixerEarning(
                        convertToEnglish(startDay),
                        convertToEnglish(endDay)
                    )

                ) // first startDate EndDate 2021/11/01)

            } else {
//            it will goes to 16 to end of the month day bunch
                val leftDateCalendar = Calendar.getInstance()
                leftDateCalendar.time = it.time
                leftDateCalendar.set(Calendar.DAY_OF_MONTH, 16)

                val lastDayOfMonthCalendar = Calendar.getInstance()
                lastDayOfMonthCalendar.time = it.time
                lastDayOfMonthCalendar.add(Calendar.MONTH, 1)
                lastDayOfMonthCalendar[Calendar.DAY_OF_MONTH] = 1
                lastDayOfMonthCalendar.add(Calendar.DATE, -1)

                allDateSetOn(
                    leftDateFormat.format(leftDateCalendar.time),
                    rightDateFormat.format(lastDayOfMonthCalendar.time)
                )

                val startDay =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.ENGLISH
                    ).format(leftDateCalendar.time)
                        .toString()
                val endDay =
                    SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(
                        lastDayOfMonthCalendar.time
                    )
                        .toString()
                viewModel.fixerEarning(
                    FixerEarning(startDay, endDay) // first startDate EndDate 2021/11/01
                )
            }
        }
    }
}
