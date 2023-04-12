package com.massttr.provider.ui.language.login.register.verify.fixerRegistration

import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.Subcategory
import com.common.data.prefs.SharedPref
import com.massttr.provider.MyApp
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemCategoryBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi


@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class SubCategoryAdapter :
    BaseAdapter<ListItemCategoryBinding, Subcategory>(R.layout.list_item_category) {

    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }

    override fun setClickableView(binding: ListItemCategoryBinding): List<View?> {
        return listOf(binding.root)
    }

    override fun onBind(
        binding: ListItemCategoryBinding,
        position: Int,
        item: Subcategory,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            if(pref.isArabic==true){
                tvCategoryTitle.text = item.ar_name
            }else{
                tvCategoryTitle.text = item.en_name
            }

        }
    }
}