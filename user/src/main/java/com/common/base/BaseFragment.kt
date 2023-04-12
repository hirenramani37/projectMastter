package com.common.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.common.data.prefs.SharedPref
import com.common.multilanguage.LocaleManager
import com.massttr.user.utils.AppLoader
import com.google.android.material.snackbar.Snackbar
import com.massttr.user.MyApp
import kotlinx.coroutines.channels.ReceiveChannel


abstract class BaseFragment<VB : ViewDataBinding>(@LayoutRes val layoutRes: Int) : Fragment() {

    protected lateinit var binding: VB
    val mHandler by lazy { Handler() }
    protected val listSubscription = ArrayList<ReceiveChannel<*>>()
    private val appLoader: AppLoader by lazy { AppLoader(requireActivity()) }
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }
    // val dao: AppDao by lazy { App.getInstance().getDao() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }


    protected fun setNewLocale(language: String) {
        LocaleManager.setNewLocale(requireContext(), language)
            //requireActivity().recreate()
    }

    open fun showMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    open fun showError(errorMessage: String) {
        showMessage(errorMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        appLoader.dismiss()
        listSubscription.forEach { it.cancel() }
    }

    protected fun handleError(it: Throwable) {
        val activity = requireActivity()
        if (activity is BaseActivity<*>) activity.handleError(it)
    }

    protected fun updateLoaderUI(isShow: Boolean) {
        if (isShow) appLoader.show() else appLoader.dismiss()
    }



}

