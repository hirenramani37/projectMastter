package com.massttr.provider.ui.language.login.register.verify.fixerRegistration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.Categories
import com.common.data.network.model.Subcategory
import com.common.data.network.model.request.Category
import com.massttr.user.utils.PRM_SELECTED_CATEGORY
import com.massttr.user.utils.PRM_SELECTED_SUBCATEGORY
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityFixerServiceBinding
import com.massttr.user.utils.setShakeError
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class FixerServiceActivity :
    BaseActivity<ActivityFixerServiceBinding>(R.layout.activity_fixer_service),
    View.OnClickListener {

    private val viewModel: FixerRegisterViewModel by viewModels()
    private lateinit var selectServiceAdapter: SelectServiceAdapter
    private lateinit var selectSubCategoryAdapter: SelectSubCategoryAdapter
    private var categoryData = ArrayList<Categories>()
    private var subCategoryData = ArrayList<Subcategory>()
    private var subCategoryDataReg = ArrayList<Subcategory>()
    private var categoryId: String = ""


    companion object {
        const val SUB_CATEGORY_IDS = "SUB_CATEGORY_IDS"
        const val SUB_CATEGORY_FROM_REGISTRATION = "SUB_CATEGORY_FROM_REGISTRATION"
        const val CATEGORY = "SELECTED_CATEGORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setObserver()
        clickListener()
    }

    private fun init() {
        binding.toolbar.tvTitle.text = "Service"
        selectSubCategoryAdapter = SelectSubCategoryAdapter()
        binding.rvSelectService.adapter = selectSubCategoryAdapter
        selectServiceAdapter = SelectServiceAdapter(this@FixerServiceActivity)
        binding.rvSelectService.adapter = selectServiceAdapter
        if (intent.hasExtra(CATEGORY)) {

            categoryData = intent.getSerializableExtra(CATEGORY) as ArrayList<Categories>
            Timber.e("List%s", categoryData.size)

            selectServiceAdapter.setItemClickListener { view, i, continentsItem ->
                if (continentsItem.isSelected) {
                    continentsItem.isSelected = false
                    runOnUiThread {
                        run breaker@{
                            categoryData.forEachIndexed { index, CategoryDataSave ->
                                if (CategoryDataSave.id == continentsItem.id) {
                                    categoryData.removeAt(index)
                                    return@breaker
                                }
                            }
                        }
                    }
                } else {
                    continentsItem.isSelected = true
                    categoryData.add(continentsItem)
                }
                selectServiceAdapter.notifyItemChanged(i)

            }
            viewModel.getService()
        }
        if (intent.hasExtra(SUB_CATEGORY_FROM_REGISTRATION)) {
            subCategoryDataReg = intent.getSerializableExtra(SUB_CATEGORY_FROM_REGISTRATION) as ArrayList<Subcategory>



            //selectSubCategoryAdapter.addAll(subCategoryDataReg)

            // selected SubCategory Data
            selectSubCategoryAdapter.setItemClickListener { _, i, continentsItem ->
                subCategoryDataReg[i].isSelected = !continentsItem.isSelected!!
                selectSubCategoryAdapter.notifyDataSetChanged()
            }
        }

        if (intent.hasExtra(SUB_CATEGORY_IDS)) {
            subCategoryData =
                intent.getSerializableExtra(SUB_CATEGORY_IDS) as ArrayList<Subcategory>
            selectSubCategoryAdapter = SelectSubCategoryAdapter()
            binding.rvSelectService.adapter = selectSubCategoryAdapter
            Timber.e("subCategoryData%s", subCategoryData.size)
            //selectSubCategoryAdapter.addAll(subCategoryData)

            selectSubCategoryAdapter.setItemClickListener { _, i, continentsItem ->
                if (continentsItem.isSelected) {
                    continentsItem.isSelected = false
                    runOnUiThread {
                        run breaker@{
                            subCategoryData.forEachIndexed { index, subCategoryDataSave ->
                                if (subCategoryDataSave.id == continentsItem.id) {
                                    // selectSubCategoryAdapter.list[i].isSelected = false
                                    subCategoryData.removeAt(index)
                                    return@breaker
                                }
                            }
                        }
                    }
                } else {
                    continentsItem.isSelected = true
                    subCategoryData.add(continentsItem)
                }

                selectSubCategoryAdapter.notifyItemChanged(i)
            }
//            runOnUiThread {
//                run breaker@{
//                    selectSubCategoryAdapter.list.forEachIndexed { index, subCategory ->
//
//                        subCategoryData.forEachIndexed { index, subCategoryDataSave ->
//                            if (subCategoryDataSave.id == subCategory.id) {
//                                subCategory.isSelected = false
//                                return@breaker
//                            }
//                        }
//
//                    }
//                }
//            }
        }

        if (intent.hasExtra("category_id")) {
            categoryId = intent.getStringExtra("category_id") ?: ""
            Timber.e("category: ${categoryId}")
            subCategoryList(categoryId)
        }
    }

    private fun setCheck(subCategoryData1: ArrayList<Subcategory>): ArrayList<Subcategory> {
        for (i in subCategoryData) {
            for (j in subCategoryData1) {
                if (i.id == j.id)
                    j.isSelected = true
            }
        }
        return subCategoryData1
    }

    private fun setCheckCategory(Cdata: ArrayList<Categories>): ArrayList<Categories> {
        for (category in Cdata) {
            for (sCategory in categoryData) {
                if (category.id == sCategory.id)
                    category.isSelected = true
            }
        }
        return Cdata
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@FixerServiceActivity)
            btnDone.setOnClickListener(this@FixerServiceActivity)
        }
    }

    private fun setObserver() {
        viewModel.run {
            apiErrors.observe(this@FixerServiceActivity) { handleError(it) }
            appLoader.observe(this@FixerServiceActivity) { updateLoaderUI(it) }
            //get service
            service.observe(this@FixerServiceActivity) {
                selectServiceAdapter.addAll(setCheckCategory(it as ArrayList<Categories>))
            }
            // get subCategory
            subCategory.observe(this@FixerServiceActivity) {
                selectSubCategoryAdapter.addAll(setCheck(it as ArrayList<Subcategory>))
            }
        }
    }

    private fun subCategoryList(id: String) {
        val category = Category(id)
        viewModel.getSubCategory(category)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnDone -> {
                val categories = selectServiceAdapter.displayList.filter { it.isSelected }
                val subCategories = selectSubCategoryAdapter.displayList.filter { it.isSelected }
                if(categories.isNotEmpty() || subCategories.isNotEmpty()){
                    when {
                        intent.hasExtra(CATEGORY) -> {
                            val intent = Intent()
                            intent.putExtra(PRM_SELECTED_CATEGORY, categoryData)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                        intent.hasExtra(SUB_CATEGORY_FROM_REGISTRATION) -> {
                            val intent = Intent()
                            intent.putExtra("subData", subCategoryData)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                        else -> {
                            val intent = Intent()
                            intent.putExtra(PRM_SELECTED_SUBCATEGORY, subCategoryData)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }else{
                    setShakeError("Please Select Item")
                }

            }
        }
    }
}