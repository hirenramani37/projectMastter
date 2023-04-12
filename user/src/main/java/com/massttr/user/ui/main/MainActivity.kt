package com.massttr.user.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.common.base.BaseActivity
import com.common.multilanguage.LocaleManager
import com.massttr.user.R
import com.massttr.user.databinding.ActivityMainBinding
import com.massttr.user.utils.BUS_EVENT_CHAT_COUNT
import com.massttr.user.utils.BUS_EVENT_NOTIFICATION_COUNT
import com.massttr.user.utils.EventBus
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber


@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var navController: NavController
    private var chatCount: Int = 0
    private var notificationCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBusEvent()
        navController = Navigation.findNavController(this, R.id.fragmentMain)
        binding.bnvMain.setupWithNavController(navController)
        binding.bnvMain.itemIconTintList = null

        notificationCount(pref.notificationCount ?: 0)
        inboxCount(pref.chatCount ?: 0)
    }


    private fun getBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey("Language")) {
                if (it.getBoolean("Language", false)) { // true Iran
                    setNewLocale(LocaleManager.Arabic)
                } else {
                    setNewLocale(LocaleManager.ENGLISH)
                }
            } else if (it.containsKey("UpdateProfile")) {
                val updateProfile = it.getBoolean("UpdateProfile", false)
                if (updateProfile) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        navController.navigate(R.id.navMyProfileFragment)
                    }, 50)
                }
            } else if (it.containsKey(BUS_EVENT_CHAT_COUNT)) {
                val readCountChat = it.getInt(BUS_EVENT_CHAT_COUNT, 0)
                Timber.e("BUS_EVENT_CHAT_COUNT $readCountChat")
                pref.chatCount = readCountChat
                chatCount = readCountChat
                inboxCount(readCountChat)

//                val bundle = Bundle()
//                bundle.putInt("count",readCountChat)
//                EventBus.publish(bundle)
            } else if (it.containsKey(BUS_EVENT_NOTIFICATION_COUNT)) {
                val readCountNoti = it.getInt(BUS_EVENT_NOTIFICATION_COUNT, 0)
                pref.notificationCount = readCountNoti
                notificationCount = readCountNoti
                notificationCount(readCountNoti)
            }
//            }else if(it.containsKey(("count"))){
//                val count = it.getInt("count",0)
//                Timber.e("countHiren$count")
//                chatCount = count
//                handler.postDelayed({
//                    if(chatCount == 0){
//                        binding.bnvMain.getOrCreateBadge(R.id.navInboxFragment).isVisible = false
//                    }else{
//                        val chatC = count
//                        binding.bnvMain.getOrCreateBadge(R.id.navInboxFragment).isVisible = true
//                        binding.bnvMain.getOrCreateBadge(R.id.navInboxFragment).setNumber(chatC)
//                    }
//                },5)
//            }
        }
    }

    private fun notificationCount(notificationCount: Int) {
        if (notificationCount == 0) {
            binding.bnvMain.getOrCreateBadge(R.id.navNotificationFragment).isVisible = false
        } else {
            binding.bnvMain.getOrCreateBadge(R.id.navNotificationFragment).isVisible = true
            binding.bnvMain.getOrCreateBadge(R.id.navNotificationFragment).number = notificationCount
        }
    }

    private fun inboxCount(chatCount: Int) {
        Timber.d("chatCount$chatCount")
        runOnUiThread {
            if (chatCount == 0) {
                binding.bnvMain.getOrCreateBadge(R.id.navInboxFragment).isVisible = false
            } else {
                binding.bnvMain.getOrCreateBadge(R.id.navInboxFragment).isVisible = true
                binding.bnvMain.getOrCreateBadge(R.id.navInboxFragment).number = chatCount
            }
        }
        //binding.bnvMain.getOrCreateBadge(R.id.navInboxFragment).number = chatCount
    }
}