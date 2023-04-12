package com.massttr.provider.ui.main.myprofiles.manages.manageServices

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.base.BaseActivity
import com.common.data.network.model.Categories
import com.common.data.network.model.Subcategory
import com.common.data.network.model.request.Category
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityManageServiceBinding
import com.massttr.provider.ui.language.login.register.verify.fixerRegistration.CategoryAdapter
import com.massttr.provider.ui.language.login.register.verify.fixerRegistration.FixerServiceActivity
import com.massttr.provider.ui.language.login.register.verify.fixerRegistration.SubCategoryAdapter
import com.massttr.user.utils.PRM_SELECTED_CATEGORY
import com.massttr.user.utils.PRM_SELECTED_SUBCATEGORY
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import okhttp3.MultipartBody
import org.jetbrains.anko.toast
import timber.log.Timber


@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class ManageServiceActivity :
    BaseActivity<ActivityManageServiceBinding>(R.layout.activity_manage_service),
    View.OnClickListener {

    private val viewModel: ManageServiceActivityViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var subCategoryAdapter: SubCategoryAdapter

    private var subCategoryData = ArrayList<Subcategory>()

    private var categoryData = ArrayList<Categories>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            appLoader.observe(this@ManageServiceActivity) { updateLoaderUI(it) }
            apiErrors.observe(this@ManageServiceActivity) { handleError(it) }
            service.observe(this@ManageServiceActivity) { cat ->// get category
//                run breaker@{
//                    cat.forEach { category ->
//                        pref.userInfo?.services?.forEach { service ->
//                            if (service.category_id == category.id) {
//                                Timber.e("category ${service.subcategory_id}")
//                                service.subcategory_id
//                                //  subCategoryList(service.subcategory_id.joinToString(","))
//                                // return@breaker
//                            }
//
//                        }
//                    }
//                }
             pref.userInfo?.services?.map { it.category_id }?.let { it1 -> subCategoryList(it1.joinToString()) }

//                    pref.userInfo?.services?.filter { it.category_id==category.id }?.map { it.category_id }
//                        ?.let { it1 -> subCategoryList(it1.joinToString(",")) }

//                    val getData = pref.userInfo?.services?.filter { it.category_id==category.id }?.map { it.category_id }?.joinToString(",")
//                        if(getData?.isNotEmpty() == true) {
//                            subCategoryList(getData)
//                            Timber.e("category $getData")
//                        }




                categoryData = cat as ArrayList<Categories>
                categoryAdapter.addAll(setCheckMain(categoryData).filter { it.isSelected })
            }
            subCategory.observe(this@ManageServiceActivity) {
                subCategoryData = it as ArrayList<Subcategory>
                Timber.d("SubCategory--> ${subCategoryData.size}")
               // subCategoryAdapter.addAll(subCategoryData)
                 subCategoryAdapter.addAll(setCheck(subCategoryData).filter { it.isSelected })
            }
            upDateCategories.observe(this@ManageServiceActivity) {
                // showMessage("Updated")
                updateProfile()

            }
            getProfile.observe(this@ManageServiceActivity) {
                val temp = pref.userInfo
                temp?.services = it.services
                temp?.skill_ids = it.skill_ids
                temp?.description = it.description
                pref.userInfo = temp
                onBackPressed()
            }
        }
    }


    private fun setCheckMain(allServices: ArrayList<Categories>): Collection<Categories> {
        for (i in allServices) {
            for (j in pref.userInfo!!.services) {
                if (i.id == j.category_id) {
                    i.isSelected = true
                }
            }
        }
        return allServices
    }

    private fun setCheck(subCategoryData: ArrayList<Subcategory>): ArrayList<Subcategory> {
        for (i in subCategoryData) {
            for (j in pref.userInfo!!.services) {
                if (i.id == j.subcategory_id) {
                    i.isSelected = true
                }
            }
        }
        return subCategoryData
    }


    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@ManageServiceActivity)
            btnSubmit.setOnClickListener(this@ManageServiceActivity)
            btnAddService.setOnClickListener(this@ManageServiceActivity)
            btnAddSubCategories.setOnClickListener(this@ManageServiceActivity)
        }
    }


    private fun init() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.manage_service)
            pref.userInfo.let {
                tvDescribe.setText(it?.description)
            }
            categoryAdapter = CategoryAdapter(this@ManageServiceActivity)  // Category box
            rvService.adapter = categoryAdapter
            val linearLayoutManager = LinearLayoutManager(
                this@ManageServiceActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            rvService.layoutManager = linearLayoutManager
            subCategoryAdapter = SubCategoryAdapter() // SubCategory box
            rvSubCategoryList.adapter = subCategoryAdapter
            val subCategoryHorizontal = LinearLayoutManager(
                this@ManageServiceActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            rvSubCategoryList.layoutManager = subCategoryHorizontal
            categoryAdapter.setItemClickListener { view, i, categoryData ->
                when (view.id) {
                    R.id.ivDeleteCategory -> {
                        // run breaker@{
                        // subCategoryData.forEach {
                        //  if(categoryData.id==it.category_id){
                        categoryAdapter.displayList.removeAt(i)
                        categoryAdapter.notifyDataSetChanged()
                        //   return@breaker
                        //   }

                        // }
                        // }


                        val categoryId =
                            categoryAdapter.displayList.filter { it.isSelected }.map { it.id }
                                .joinToString(",")
                        subCategoryList(categoryId)

                    }
                }
            }

            subCategoryAdapter.setItemClickListener { _, i, subCategory ->
                subCategoryData.forEachIndexed { index, subCategoryData ->
                    if (subCategory.id == subCategoryData.id) {
                        subCategoryData.isSelected = false
                    }
                }
                subCategoryAdapter.displayList.removeAt(i)
                subCategoryAdapter.notifyDataSetChanged()
            }
        }

        //category
        viewModel.getService()

        //Timber.e("List: $subCategoryList")
    }

    private fun updateProfile() {
        viewModel.getProfile()
    }

    private fun subCategoryList(id: String) {
        val category = Category(id)
        viewModel.getSubCategory(category)
    }

    private fun setUpdateCategories() {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart(
            "subcategory_ids",
            subCategoryAdapter.displayList.map { it.id }.joinToString(",")
        )
        builder.addFormDataPart("comments", binding.tvDescribe.text.toString().trim())
        builder.addFormDataPart("env", "test")
        viewModel.upDateCategory(builder.build())
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnSubmit -> {
                setUpdateCategories()
            }
            R.id.btnAddService -> {
                val intent = Intent(this, FixerServiceActivity::class.java)
                intent.putExtra(FixerServiceActivity.CATEGORY, categoryAdapter.displayList)
                startActivityForResult(intent, 101)
                //resultLauncher.launch(intent)
            }
            R.id.btnAddSubCategories -> {
                val categoryId = categoryAdapter.displayList.filter { it.isSelected }.map { it.id }
                    .joinToString(",")
                if (categoryAdapter.displayList.map { it.id }.joinToString().isEmpty()) {
                    toast("Please Select Service")
                } else {
                    Timber.e("--------------subcategory%s", subCategoryAdapter.displayList)
                    val intent = Intent(this, FixerServiceActivity::class.java)
                    intent.putExtra(
                        FixerServiceActivity.SUB_CATEGORY_IDS,
                        subCategoryAdapter.displayList
                    )
                    intent.putExtra("category_id", categoryId)
                    startActivityForResult(intent, 200)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //  categoryAdapter.clearAll()
            when (requestCode) {
                101 -> {
                    if (resultCode == Activity.RESULT_OK) {
                        categoryAdapter.clearAll()
                        subCategoryAdapter.clearAll()
                        val alCategory =
                            data?.getSerializableExtra(PRM_SELECTED_CATEGORY) as ArrayList<Categories>

                        val alCatSelected = alCategory.filter { it.isSelected }.toList()

                        alCatSelected.let { categoryAdapter.addAll(it) }
                        alCategory.forEachIndexed { _, selectdata ->
                            categoryAdapter.displayList.forEachIndexed { index, cdata ->
                                if (selectdata.id == cdata.id) {
//                                    if(subCategoryAdapter.displayList.size==0){
//
//                                    }else{
//                                        subCategoryList(categoryAdapter.displayList.map { it.id }
//                                            .joinToString())
//                                    }

                                }
                            }
                        }
                    }
                }
                200 -> {
                    if (resultCode == Activity.RESULT_OK) {
                        //  subCategoryAdapter.clearAll()
                        val alSubCategory =
                            data?.getSerializableExtra(PRM_SELECTED_SUBCATEGORY) as ArrayList<Subcategory>
                        val alSubCatSelected = alSubCategory.filter { it.isSelected }.toList()

                        alSubCatSelected.let { subCategoryAdapter.addAll(it) }
                    }
                }
            }
        }
    }


}
