package com.massttr.provider.ui.language.login.register.verify.fixerRegistration.success

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.utils.startSocketService
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivitySuccessfulBinding
import com.massttr.provider.ui.main.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class SuccessfullyActivity :
    BaseActivity<ActivitySuccessfulBinding>(R.layout.activity_successful),
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