package com.massttr.provider


import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate
import com.common.data.prefs.SharedPref
import com.common.multilanguage.LocaleManager
import com.massttr.user.utils.startSocketService
import com.massttr.user.utils.stopSocketService
import com.google.gson.Gson
import com.massttr.user.utils.NotificationChannelHelper
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.HttpUrlConnectionDownloader
import com.tonyodev.fetch2core.Downloader
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber
import java.lang.Exception

/**
 * Created by Hiren on 27-08-2021.
 */

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class MyApp : Application(), LifecycleObserver {

    private lateinit var pref: SharedPref
    private lateinit var notificationHelper: NotificationChannelHelper

    private val localizationDelegate = LocalizationApplicationDelegate()
    private lateinit var fetchConfiguration: FetchConfiguration

    companion object {
        private lateinit var mInstance: MyApp

        @Synchronized
        fun getInstance(): MyApp = mInstance
    }


    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        mInstance = this
        if (BuildConfig.DEBUG) Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return "(${element.fileName}:${element.lineNumber})#${element.methodName}"
            }
        })
        notificationHelper = NotificationChannelHelper.getInstance(this)
        pref = SharedPref(applicationContext, Gson())

        fetchConfiguration =
            FetchConfiguration.Builder(this)
                .enableRetryOnNetworkGain(true)
                .setHttpDownloader(HttpUrlConnectionDownloader(Downloader.FileDownloaderType.PARALLEL)) // OR
                .build()
        Fetch.setDefaultInstanceConfiguration(fetchConfiguration)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun getFetchConfiguration(): FetchConfiguration = fetchConfiguration

    fun getPref() = pref

    fun getNotificationHelper() = notificationHelper

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    override fun getResources(): Resources {
        return localizationDelegate.getResources(baseContext, super.getResources())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(if (base != null) LocaleManager.setLocale(base) else base)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }

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
