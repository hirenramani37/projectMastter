package com.massttr.provider.ui.language

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.common.multilanguage.LocaleManager
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityLanguageBinding
import com.massttr.provider.ui.language.login.LoginActivity
import com.massttr.user.utils.setShakeError
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class LanguageActivity : BaseActivity<ActivityLanguageBinding>(R.layout.activity_language),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clickListener()
    }

    private fun clickListener() {
        binding.run {
            bContinue.setOnClickListener(this@LanguageActivity)
            rbArabic.setOnClickListener(this@LanguageActivity)
            rbEnglish.setOnClickListener(this@LanguageActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bContinue -> validate()
            R.id.rbEnglish -> {
                binding.run {
                    rbArabic.isChecked = false
                    rbEnglish.isChecked = true
                    setNewLocale(LocaleManager.ENGLISH)
                    pref.selectLanguage = "en"
                    pref.isArabic = false
                }
            }
            R.id.rbArabic -> {
                binding.run {
                    rbArabic.isChecked = true
                    rbEnglish.isChecked = false
                    setNewLocale(LocaleManager.ARABIC)
                    pref.selectLanguage = "ar"
                    pref.isArabic = true
                }
            }
        }
    }


    private fun validate() {
        if (binding.rbArabic.isChecked || binding.rbEnglish.isChecked) {
            pref.isLanguage = true
            startActivity<LoginActivity>()
        } else setShakeError(getString(R.string.please_select_the_language))
    }
}