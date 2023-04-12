package com.massttr.user.ui.language

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.common.base.BaseActivity
import com.common.multilanguage.LocaleManager
import com.massttr.user.R
import com.massttr.user.databinding.ActivityLanguageBinding
import com.massttr.user.ui.language.login.LoginActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity


@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
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
                }
            }
            R.id.rbArabic -> {
                binding.run {
                    rbArabic.isChecked = true
                    rbEnglish.isChecked = false
                    setNewLocale(LocaleManager.Arabic)
                }
            }
        }
    }

    private fun validate() {
        if (binding.rbArabic.isChecked || binding.rbEnglish.isChecked)
            startActivity<LoginActivity>()
         else
            Toast.makeText(applicationContext, "Please select Language", Toast.LENGTH_LONG).show()
    }
}