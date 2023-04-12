package com.massttr.provider.ui.language.login.register.verify.fixerRegistration

import android.content.Context
import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.Categories
import com.common.data.network.model.Document
import com.common.data.prefs.SharedPref
import com.massttr.provider.MyApp
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemCategoryBinding
import com.massttr.provider.databinding.ListItemSubCategoryBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi


@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class CategoryAdapter(val context: Context) : BaseAdapter<ListItemCategoryBinding,Categories>(R.layout.list_item_category) {


    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }

    override fun setClickableView(binding: ListItemCategoryBinding): List<View?> {
        return listOf(binding.ivDeleteCategory)
    }

    override fun onBind(
        binding: ListItemCategoryBinding,
        position: Int,
        item: Categories,
        payloads: MutableList<Any>?
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