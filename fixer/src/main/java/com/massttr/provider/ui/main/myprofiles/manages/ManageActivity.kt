package com.massttr.provider.ui.main.myprofiles.manages

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityManageBinding
import com.massttr.provider.ui.main.myprofiles.manages.documents.DocumentsActivity
import com.massttr.provider.ui.main.myprofiles.manages.manageServices.ManageServiceActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class ManageActivity : BaseActivity<ActivityManageBinding>(R.layout.activity_manage),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
    }

    private fun clickListener() {
        binding.run {
            tvDocuments.setOnClickListener(this@ManageActivity)
            tvManageService.setOnClickListener(this@ManageActivity)
            toolBar.imgBack.setOnClickListener(this@ManageActivity)
        }
    }

    private fun initView() {
        binding.toolBar.tvTitle.text = getString(R.string.manage)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDocuments -> startActivity<DocumentsActivity>()
            R.id.tvManageService -> startActivity<ManageServiceActivity>()
            R.id.imgBack -> onBackPressed()
        }
    }
}