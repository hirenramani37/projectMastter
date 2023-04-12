package com.massttr.user.ui.language.login.register.create_profile.success_create_profile

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.utils.startSocketService
import com.massttr.user.R
import com.massttr.user.databinding.ActivitySuccessfullyBinding
import com.massttr.user.ui.main.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class SuccessfullyActivity : BaseActivity<ActivitySuccessfullyBinding>(R.layout.activity_successfully),
    View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clickListener()
    }

    private fun clickListener() {
        binding.btnDone.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnDone -> {
                startSocketService()
                startActivity<MainActivity>()
                finishAffinity()
            }
        }
    }
}