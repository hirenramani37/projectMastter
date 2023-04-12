package com.massttr.provider.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.common.base.BaseActivity
import com.common.multilanguage.LocaleManager
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityMainBinding
import com.massttr.provider.ui.main.myprofiles.manages.documents.DocumentsActivity
import com.massttr.user.utils.BUS_EVENT_CHAT_COUNT
import com.massttr.user.utils.BUS_EVENT_NOTIFICATION_COUNT
import com.massttr.user.utils.EventBus
import com.massttr.user.utils.JOB_ACCEPT_ACTIVITY
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var navController: NavController
    private var chatCount: Int = 0
    private var notificationCount: Int = 0
    // private val viewModel: MainActivityViewModel by viewModels()

    companion object {
        var flag = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(this, R.id.fragmentMain)
        binding.bnvMain.setupWithNavController(navController)
        binding.bnvMain.itemIconTintList = null
        //  binding.bnvMain.menu.forEach { it.isEnabled=false }
        checkFlag()
        getBusEvent()

        notificationCount(pref.notificationCount ?: 0)
        inboxCount(pref.chatCount ?: 0)
    }

    private fun checkFlag() {
        if (intent.hasExtra("MyUpcoming")) {
            flag = intent.getBooleanExtra("MyUpcoming", false)
            navController.navigate(R.id.action_availableTask)
        }
        if (intent.hasExtra("UpdateProfile")) {
            intent.getBooleanExtra("UpdateProfile", false)
            navController.navigate(R.id.action_profile)
        }
        if (intent.hasExtra("DOCUMENTSTATUS")) {
            val status = intent.getBooleanExtra("DOCUMENTSTATUS", false)
            if (status) {
                val intent = Intent(applicationContext, DocumentsActivity::class.java)
                startActivity(intent)
            }

        }
    }


    private fun getBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey("Language")) {
                if (it.getBoolean("Language", false)) { // true Iran
                    setNewLocale(LocaleManager.ARABIC)
                } else {
                    setNewLocale(LocaleManager.ENGLISH)
                }
            } else if (it.containsKey(JOB_ACCEPT_ACTIVITY)) {
                if (it.getBoolean(JOB_ACCEPT_ACTIVITY, false)) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        navController.navigate(R.id.navSettingFragment)
                    }, 50)
                }
            } else if (it.containsKey("END_TASK")) {
                if (it.getBoolean("END_TASK", false)) {
                    Timber.e("END")
                    Handler(Looper.getMainLooper()).postDelayed({
                        navController.navigate(R.id.navHomeFragment)
                    }, 50)
                }
            } else if (it.containsKey("ViewReceipt")) {
                if (it.getBoolean("ViewReceipt", false)) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        navController.navigate(R.id.action_availableTask)
                    }, 50)
                }
            } else if (it.containsKey("UpcomingFix")) {
                if (it.getBoolean("UpcomingFix", false)) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        navController.navigate(R.id.navSettingFragment)
                    }, 50)
                }
            } else if (it.containsKey("UpdateProfile")) {
                if (it.getBoolean("UpdateProfile", false)) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        navController.navigate(R.id.navMyProfileFragment)
                    }, 50)
                }
            } else if (it.containsKey(BUS_EVENT_CHAT_COUNT)) {
                val readCountChat = it.getInt(BUS_EVENT_CHAT_COUNT, 0)
                pref.chatCount = readCountChat
                chatCount = readCountChat
                inboxCount(readCountChat)
            } else if (it.containsKey(BUS_EVENT_NOTIFICATION_COUNT)) {
                val readCountNoti = it.getInt(BUS_EVENT_NOTIFICATION_COUNT, 0)
                pref.notificationCount = readCountNoti
                notificationCount = readCountNoti
                notificationCount(readCountNoti)
            }
        }
    }

    private fun notificationCount(notificationCount: Int) {
//        binding.bnvMain.getOrCreateBadge(R.id.navNotificationFragment).number = notificationCount
        binding.run {
            if (notificationCount == 0) {
                bnvMain.getOrCreateBadge(R.id.navNotificationFragment).isVisible = false
            } else {
                bnvMain.getOrCreateBadge(R.id.navNotificationFragment).isVisible = true
                bnvMain.getOrCreateBadge(R.id.navNotificationFragment).number = notificationCount
            }
        }
    }

    private fun inboxCount(chatCount: Int) {
        binding.run {
            if (chatCount == 0) {
                bnvMain.getOrCreateBadge(R.id.navMyProfileFragment).isVisible = false
            } else {
                bnvMain.getOrCreateBadge(R.id.navMyProfileFragment).isVisible = true
                bnvMain.getOrCreateBadge(R.id.navMyProfileFragment).number = chatCount
            }
        }
    }
}
