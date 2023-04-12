package com.massttr.provider.ui.language.login.register.verify.fixerRegistration

import android.content.Context
import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.Categories
import com.common.data.prefs.SharedPref
import com.massttr.provider.MyApp
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemSubCategoryBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi


@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class SelectServiceAdapter(val context: Context) :
    BaseAdapter<ListItemSubCategoryBinding, Categories>(R.layout.list_item_sub_category) {


    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }

    override fun setClickableView(binding: ListItemSubCategoryBinding): List<View?> {
        return listOf(binding.cbService)
    }

    override fun onBind(
        binding: ListItemSubCategoryBinding,
        position: Int,
        item: Categories,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            cbService.isChecked = item.isSelected

            if(pref.isArabic==true){
                tvNameCategory.text = item.ar_name
            }else{
                tvNameCategory.text = item.en_name
            }
//          if(item.en_name=="en")
//              tvNameCategory.text = item.en_name
//          else
//              tvNameCategory.text = item.ar_name
        }
    }
}