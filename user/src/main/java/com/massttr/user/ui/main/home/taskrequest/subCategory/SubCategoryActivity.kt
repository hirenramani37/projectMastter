package com.massttr.user.ui.main.home.taskrequest.subCategory

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.Subcategory
import com.massttr.user.utils.showErrorToast
import com.massttr.user.R
import com.massttr.user.databinding.ActivitySubCategoryBinding

class SubCategoryActivity :
    BaseActivity<ActivitySubCategoryBinding>(R.layout.activity_sub_category), View.OnClickListener {
    private val viewModel: SubCategoryActivityViewModel by viewModels()
    private var subCategoryAdapter: SubCategoryAdapter? = null
    private var subcategoryData: Int = 0
    private var categoryEditText = ArrayList<Subcategory>()

    companion object {
        const val RESULT = "Result"
        const val SUB_CATEGORY_LIST = "SUB_CATEGORY_LIST"
        const val PRM_CATEGORY_EDIT_TEXT = "PRM_CATEGORY_EDIT_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        initView()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@SubCategoryActivity) { handleError(it) }
            appLoader.observe(this@SubCategoryActivity) { updateLoaderUI(it) }
            category.observe(this@SubCategoryActivity) {
                it.data?.forEach { category ->
                    category.subcategory?.forEach { subcategory ->
                        if (subcategoryData == subcategory.category_id) {
                            subCategoryAdapter?.addAll(category.subcategory)
                        }
                    }
                }
            }
        }
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@SubCategoryActivity)
            btnDone.setOnClickListener(this@SubCategoryActivity)
        }
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.select_subcategory)
            subCategoryAdapter = SubCategoryAdapter()
            rvSubCategoryList.adapter = subCategoryAdapter
            if (intent.hasExtra(PRM_CATEGORY_EDIT_TEXT)) {
                subcategoryData = intent.getIntExtra(PRM_CATEGORY_EDIT_TEXT, 0)
            }

            if (intent.hasExtra(SUB_CATEGORY_LIST)) {
                categoryEditText =
                    intent?.getSerializableExtra(SUB_CATEGORY_LIST) as ArrayList<Subcategory>
            }

            subCategoryAdapter?.setItemClickListener { _, i, subCategoryData ->
                if (subCategoryData.isSelected) {
                    subCategoryData.isSelected = false
                    runOnUiThread {
                        run breaker@{
                            categoryEditText.forEachIndexed { index, subCategoryDataSave ->
                                if (subCategoryDataSave.id == subCategoryData.id) {
                                    categoryEditText.removeAt(index)
                                    return@breaker
                                }
                            }
                        }
                    }
                } else {
                    subCategoryData.isSelected = true
                    categoryEditText.add(subCategoryData)
                }
                subCategoryAdapter?.notifyItemChanged(i)
            }
            getAllSubCategory()
        }
    }

    private fun getAllSubCategory() {
        viewModel.category()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnDone -> {
                val selectedList =
                    ArrayList(subCategoryAdapter?.displayList?.filter { it.isSelected }.orEmpty())
                if (selectedList.isNullOrEmpty())
                    return getString(R.string.please_select_category).showErrorToast()
                val intent = Intent()
                intent.putExtra(RESULT, selectedList)
                setResult(Activity.RESULT_OK, intent)
                onBackPressed()
            }
        }
    }
}