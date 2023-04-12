package com.massttr.provider.ui.main.myprofiles.helpCenter.faq

import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.FaqResponse
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemFaqBinding
import com.massttr.user.utils.gone
import com.massttr.user.utils.toHtml
import com.massttr.user.utils.visible

class FaqAdapter(private val lang: Boolean?) :
    BaseAdapter<ListItemFaqBinding, FaqResponse>(R.layout.list_item_faq) {
    override fun setClickableView(binding: ListItemFaqBinding): List<View?> {
        return listOf()
    }

    override fun onBind(
        binding: ListItemFaqBinding,
        position: Int,
        item: FaqResponse,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            if (lang == true) {
                tvFaqQuestion.text = item.ar_question.toHtml()
                tvFaqAnswer.text = item.ar_answer.toHtml()
            } else {
                tvFaqQuestion.text = item.en_question.toHtml()
                tvFaqAnswer.text = item.en_answer.toHtml()
            }
            itemView.setOnClickListener {
                if (tvFaqAnswer.visibility == View.VISIBLE) {
                    tvFaqAnswer.gone()
                    tvA.gone()
                } else {
                    tvFaqAnswer.visible()
                    tvA.visible()
                }
            }
        }
    }
}

//Reference
/*tvFaqQuestion.setCompoundDrawablesWithIntrinsicBounds(
0,
0,
R.drawable.ic_minimuze_faq,
0
)*/
