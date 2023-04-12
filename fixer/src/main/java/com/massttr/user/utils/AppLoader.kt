package com.massttr.user.utils

import android.app.Activity
import android.app.Dialog
import com.massttr.provider.R


class AppLoader(activity: Activity) {
    private var loader: Dialog = Dialog(activity)

    init {
        loader.setContentView(R.layout.dialog_app_loader)
        loader.setCancelable(true)
        loader.window?.setBackgroundDrawable(null)
        loader.window?.setDimAmount(0.50f)
        loader.create()
    }

    fun show() = loader.show()

    fun dismiss() = loader.dismiss()
}