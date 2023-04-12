package com.massttr.provider.ui.main.myprofiles.helpCenter

import android.content.Context
import android.view.View
import coil.load
import com.common.base.BaseAdapter
import com.common.data.network.model.PrivacyPolicyResponse
import com.common.data.prefs.SharedPref
import com.massttr.provider.MyApp
import com.massttr.provider.R

import com.massttr.provider.databinding.ListItemHelpCenterBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class HelpCenterAdapter(val arabic: Boolean?, val context: Context) :
    BaseAdapter<ListItemHelpCenterBinding, PrivacyPolicyResponse>(R.layout.list_item_help_center) {


    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }
    override fun setClickableView(binding: ListItemHelpCenterBinding): List<View?> {
        return listOf(binding.tvPrivacyPolicy)
    }


    override fun onBind(
        binding: ListItemHelpCenterBinding,
        position: Int,
        item: PrivacyPolicyResponse,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            imgPrivacyPolicy.load(item.icon)
            if (pref.isArabic == true){
                tvPrivacyPolicy.text = item.ar_name
            }else{
                tvPrivacyPolicy.text = item.en_name
            }




//            if (position == 1) tvPrivacyPolicy.text = context.getString(R.string.terms_condition)
//            if (position == 2) tvPrivacyPolicy.text = context.getString(R.string.privacy_policy)
        }
    }
}