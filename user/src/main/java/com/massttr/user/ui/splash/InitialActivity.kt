package com.massttr.user.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivitySplashBinding
import com.massttr.user.ui.language.LanguageActivity
import com.massttr.user.ui.language.login.LoginActivity
import com.massttr.user.ui.main.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class InitialActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
    }

    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (pref.userInfo?.is_verify == 1) {
                if (pref.userInfo?.is_registered == 1) {
                    startActivity<MainActivity>()
                    finish()
                } else {
                    startActivity<LoginActivity>()
                    finish()
                }
            } else {
                startActivity<LanguageActivity>()
                finish()
            }
        }, 3000)
    }
}