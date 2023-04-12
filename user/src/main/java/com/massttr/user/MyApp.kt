package com.massttr.user

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.common.data.prefs.SharedPref
import com.common.multilanguage.LocaleManager
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.massttr.user.utils.NotificationChannelHelper
import com.massttr.user.utils.startSocketService
import com.massttr.user.utils.stopSocketService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber

/**
 * Created by Hiren on 27-08-2021.
 */

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class MyApp : Application(), LifecycleObserver {

    private lateinit var pref: SharedPref
    private lateinit var notificationHelper: NotificationChannelHelper

    companion object {
        private lateinit var mInstance: MyApp

        @Synchronized
        fun getInstance(): MyApp {
            return mInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        if (BuildConfig.DEBUG) Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                return "(${element.fileName}:${element.lineNumber})#${element.methodName}"
            }
        })
        notificationHelper = NotificationChannelHelper.getInstance(this)
        pref = SharedPref(this, Gson())
        FirebaseApp.initializeApp(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(if (base != null) LocaleManager.setLocale(base) else base)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }

    fun getNotificationHelper() = notificationHelper

    fun getPref() = pref

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onMoveToForeground() {
        Timber.d("ON_RESUME")
        try {
            startSocketService()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onMoveToBackground() {
        Timber.d("ON_PAUSE")
        stopSocketService()
    }
}