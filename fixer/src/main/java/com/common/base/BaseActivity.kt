package com.common.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.common.data.network.model.ResponseCode
import com.common.data.prefs.SharedPref
import com.common.multilanguage.LocaleManager
import com.google.android.material.snackbar.Snackbar
import com.massttr.provider.MyApp
import com.massttr.provider.R
import com.massttr.provider.ui.language.login.LoginActivity
import com.massttr.user.utils.AppLoader
import com.massttr.user.utils.PermissionUtils
import com.massttr.user.utils.setShakeError
import com.massttr.user.utils.setShakeErrorInternet
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import org.jetbrains.anko.alert
import org.jetbrains.anko.contentView
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList


@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
abstract class BaseActivity<VB : ViewDataBinding>(private val layoutRes: Int) : AppCompatActivity() {

    protected lateinit var binding: VB
    protected val listSubscription = ArrayList<ReceiveChannel<*>>()
    private val appLoader: AppLoader by lazy { AppLoader(this) }
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }

    // val dao: AppDao by lazy { App.getInstance().getDao() }
    /*private val localizationDelegate by lazy {
        LocalizationActivityDelegate(this)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorBlack))
        //AppGlobal.setStatusBarGradiant(this)
        /*localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()*/
        binding = DataBindingUtil.setContentView(this, layoutRes)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(if (base != null) LocaleManager.setLocale(base) else base)
    }

    /**
     * Use this method when user manually change the language from app.
     * Usage:
     *   setNewLocale(LocaleManager.ENGLISH)
     *   setNewLocale(LocaleManager.GERMAN)
     * */
    protected fun setNewLocale(language: String) {
        LocaleManager.setNewLocale(this, language)
        recreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        listSubscription.forEach { it.cancel() }
    }

    open fun showMessage(message: String) {
        this.contentView?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
    }

    open fun showError(errorMessage: String) {
        showMessage(errorMessage)
    }

    protected fun updateLoaderUI(isShow: Boolean) {
        if (isShow) appLoader.show() else appLoader.dismiss()
    }

    fun handleError(throwable: Throwable) {
        when (throwable) {
            is HttpException -> {
                handleResponseError(throwable)
            }
            is ConnectException, is SocketTimeoutException, is UnknownHostException -> {
                setShakeErrorInternet(getString(R.string.msg_no_internet))
                //showMessage(getString(R.string.msg_no_internet))
            }
//            is SocketTimeoutException -> {
//                showMessage(getString(R.string.time_out))
//            }
        }
    }

    private fun handleResponseError(throwable: HttpException) {
        when (throwable.code()) {
            ResponseCode.InvalidData.code -> {
                val errorRawData = throwable.response()?.errorBody()?.string()?.trim()
                if (!errorRawData.isNullOrEmpty()) {
                    val jsonObject = JSONObject(errorRawData)
                    val jObject = jsonObject.optJSONObject("errors")
                    if (jObject != null) {
                        val keys: Iterator<String> = jObject.keys()
                        if (keys.hasNext()) {
                            val msg = StringBuilder()
                            while (keys.hasNext()) {
                                val key: String = keys.next()
                                if (jObject.get(key) is String) {
                                    msg.append("- ${jObject.get(key)}\n")
                                }
                            }
                            errorDialog(msg.toString(), "Alert")
                        } else {
                            errorDialog(jsonObject.optString("message", ""))
                        }
                    } else {
                        errorDialog(JSONObject(errorRawData).optString("message"), "Alert")
                    }
                }
            }
            ResponseCode.Unauthenticated.code -> {
                val errorRawData = throwable.response()?.errorBody()?.string()?.trim()
                if (!errorRawData.isNullOrEmpty()) {
                    alert(errorRawData,
                        getString(R.string.alert)
                    ) { okButton { onAuthFail() } }.show()
                } else {
                    onAuthFail()
                }
            }
            ResponseCode.ForceUpdate.code -> {

            }
            ResponseCode.InternalServerError.code -> setShakeErrorInternet("Internal Server error")
            ResponseCode.BadRequest.code,
            ResponseCode.Unauthorized.code,
            ResponseCode.NotFound.code,
            ResponseCode.RequestTimeOut.code,
            ResponseCode.Conflict.code,
            ResponseCode.Blocked.code,
            -> {
                val errorRawData = throwable.response()?.errorBody()?.string()?.trim()
                if (!errorRawData.isNullOrEmpty()) {
                    errorDialog(JSONObject(errorRawData).optString("message", ""))
                }
            }
        }
    }

    private fun onAuthFail() {
        pref.clearAppUserData()
        startActivity<LoginActivity>()
        finishAffinity()
    }

    private fun errorDialog(optString: String?, title: String = getString(R.string.app_name)) {
        optString?.let {
            setShakeError(it)
            // alert(it, title) { okButton { } }.show()
        }
    }

    val permissionUtils: PermissionUtils by lazy {
        PermissionUtils(this)
    }

    fun setNewLanguage(lang: String) {
        //setLanguage(lang)
        updateApplicationLanguage(lang)
    }

    private fun updateApplicationLanguage(lang: String) {
        val newLocale = Locale(lang)
        val applicationConf = applicationContext.resources.configuration
        applicationConf.setLocale(newLocale)
        createConfigurationContext(applicationConf)
    }

}

