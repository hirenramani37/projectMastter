package com.common.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.common.data.network.model.ResponseCode
import com.common.data.prefs.SharedPref
import com.common.multilanguage.LocaleManager
import com.massttr.user.utils.AppLoader
import com.massttr.user.utils.PermissionUtils
import com.google.android.material.snackbar.Snackbar
import com.massttr.provider.MyApp
import com.massttr.provider.R
import com.massttr.provider.ui.language.login.LoginActivity
import com.massttr.user.utils.setShakeError
import com.massttr.user.utils.setShakeErrorInternet
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
abstract class BaseFragment<VB : ViewDataBinding>(@LayoutRes val layoutRes: Int) : Fragment() {

    protected lateinit var binding: VB
    protected val listSubscription = ArrayList<ReceiveChannel<*>>()
    private val appLoader: AppLoader by lazy { AppLoader(requireActivity()) }
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }

    // val dao: AppDao by lazy { App.getInstance().getDao() }
    lateinit var myLocale: Locale
    private lateinit var mActivity: BaseActivity<*>
    var mcontext: Context = MyApp.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

//    override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(if (base != null) LocaleManager.setLocale(base) else base)
//    }

    open fun showMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    open fun showError(errorMessage: String) {
        showMessage(errorMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        listSubscription.forEach { it.cancel() }
    }

    val permissionUtils: PermissionUtils by lazy {
        PermissionUtils(this)
    }

    /**
     * Use this method when user manually change the language from app.
     * Usage:
     *   setNewLocale(LocaleManager.ENGLISH)
     *   setNewLocale(LocaleManager.ARABIC)
     * */
    protected fun setNewLocale(language: String,context: Activity) {
        LocaleManager.setNewLocale(requireContext(), language)
        context.recreate()
    }




    protected fun handleError(it: Throwable) {
        when (it) {
            is HttpException -> {
                handleResponseError(it)
            }
            is ConnectException -> {
                showMessage(getString(R.string.msg_no_internet))
            }
            is SocketTimeoutException -> {
                requireActivity().setShakeErrorInternet(getString(R.string.time_out))
               // showMessage(getString(R.string.time_out))
            }
            is UnknownHostException->{
                requireActivity().setShakeErrorInternet(getString(R.string.msg_no_internet))
            }
        }
//        val activity = requireActivity()
//        if (activity is BaseActivity<*>) activity.handleError(it)
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
                   requireActivity().alert(
                        errorRawData,
                        getString(R.string.alert)
                    ) { okButton { onAuthFail() } }.show()
                } else {
                    onAuthFail()
                }
            }
            ResponseCode.ForceUpdate.code -> {

            }
            ResponseCode.InternalServerError.code -> errorDialog("Internal Server error")
            ResponseCode.BadRequest.code,
            ResponseCode.Unauthorized.code,
            ResponseCode.NotFound.code,
            ResponseCode.RequestTimeOut.code,
            ResponseCode.Conflict.code,
            ResponseCode.Blocked.code -> {
                val errorRawData = throwable.response()?.errorBody()?.string()?.trim()
                if (!errorRawData.isNullOrEmpty()) {
                    errorDialog(JSONObject(errorRawData).optString("message", ""))
                }
            }
        }
    }

    private fun onAuthFail() {
        pref.clearAppUserData()
            requireActivity().startActivity<LoginActivity>()
        finishAffinity(requireActivity())
    }

    private fun errorDialog(optString: String?, title: String = getString(R.string.app_name)) {
        optString?.let {
            requireActivity().setShakeError(it)
            //requireActivity().alert(it, title) { okButton { } }.show()
        }
    }

    protected fun updateLoaderUI(isShow: Boolean) {
        if (isShow) appLoader.show() else appLoader.dismiss()
    }

    /*private fun goToActivity(activity: Class<*>?) {
        startActivity(Intent(this, activity))
    }*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*>) {
            val activity = context as BaseActivity<*>?
            activity?.let { this.mActivity = it }
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (context is BaseActivity<*>) {
            val activity = context as BaseActivity<*>?
            activity?.let { this.mActivity = it }
        }
    }


}

