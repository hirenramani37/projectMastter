package com.massttr.user.ui.main.myprofile.help_center.faq

import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.Faq
import com.common.data.prefs.SharedPref
import com.massttr.user.MyApp
import com.massttr.user.R
import com.massttr.user.databinding.ListItemFaqBinding
import com.massttr.user.utils.gone
import com.massttr.user.utils.toHtml
import com.massttr.user.utils.visible

class FaqAdapter : BaseAdapter<ListItemFaqBinding, Faq>(R.layout.list_item_faq) {
    private val pref: SharedPref by lazy { MyApp.getInstance().getPref() }
    override fun setClickableView(binding: ListItemFaqBinding): List<View?> {
        return listOf()
    }

    override fun onBind(
        binding: ListItemFaqBinding,
        position: Int,
        item: Faq,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            if (pref.isLanguage == true) {
                tvFaqQuestion.text = item.en_question
                tvFaqAnswer.text = item.en_answer.toHtml()
            } else {
                tvFaqQuestion.text = item.ar_question
                tvFaqAnswer.text = item.ar_answer.toHtml()
            }
            itemView.setOnClickListener {
                if (tvFaqAnswer.visibility == View.VISIBLE) {
                    tvFaqAnswer.gone()
                    tvA.gone()
                    imgPlus.setImageResource(R.drawable.ic_plus_faq)
                } else {
                    tvFaqAnswer.visible()
                    tvA.visible()
                    imgPlus.setImageResource(R.drawable.ic_minimuze_faq)
                }
            }
        }
    }
}