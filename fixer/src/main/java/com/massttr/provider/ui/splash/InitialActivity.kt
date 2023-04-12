package com.massttr.provider.ui.splash

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.common.base.BaseActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivitySplashBinding
import com.massttr.provider.ui.language.LanguageActivity
import com.massttr.provider.ui.language.login.LoginActivity
import com.massttr.provider.ui.main.MainActivity
import com.massttr.user.utils.AppGlobal
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import timber.log.Timber


@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class InitialActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Timber.e("isVerify: ${pref.userInfo?.is_verify}")
        Timber.e("isRegister: ${pref.userInfo?.is_registered}")

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



    override fun onDestroy() {
        super.onDestroy()
    }


}
