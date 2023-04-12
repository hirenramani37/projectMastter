package com.massttr.user.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.common.data.prefs.SharedPref
import com.google.firebase.messaging.FirebaseMessaging
import com.massttr.user.BuildConfig
import com.massttr.user.MyApp
import com.massttr.user.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class AppGlobal {
    companion object {
        private val pref: SharedPref by lazy { MyApp.getInstance().getPref() }
        fun setStatusBarGradiant(context: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = context.window
                val background: Drawable? =
                    ContextCompat.getDrawable(context, R.drawable.bg_gradient_button)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
                //window.navigationBarColor = ContextCompat.getColor(context, android.R.color.transparent)
                window.setBackgroundDrawable(background)
            }
        }

        fun getFcmToken(activity: Activity, onFcmToken: (token: String) -> Unit) {
            Timber.e("fcmToken")
            if (pref.fcmToken?.trim()?.isEmpty() == true)
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    pref.fcmToken = it
                    onFcmToken(pref.fcmToken!!)
                    Timber.e("fcmToken inside $it")
                }.addOnFailureListener {
                    Timber.e("fcmToken inside $it")
                    activity.toast("something went wrong.!")
                    Timber.e(it)
                }
            else
                onFcmToken(pref.fcmToken.toString())
        }

        fun showDeniedPermissionDialog(context: Context, message: String) {
            context.alert(message) {
                positiveButton("Allow") {
                    context.startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    })
                }
                negativeButton("Deny") {}
            }.show()
        }

        fun showRationalPermissionDialog(
            context: Context,
            message: String,
            positiveClick: () -> Unit,
            negativeClick: () -> Unit
        ) {
            val alert = context.alert(message) {
                positiveButton("Allow") {
                    positiveClick()
                }
                negativeButton("Deny") {
                    negativeClick()
                }
            }
            alert.isCancelable = false
            alert.show()
        }

        fun openPdfIntent(context: Context, inVoiceUrl: String) {
            if (inVoiceUrl.isNotEmpty()) {
                val browserIntent = Intent(Intent.ACTION_VIEW)
                try {
                    browserIntent.setDataAndType(Uri.parse(inVoiceUrl), "application/pdf")
                    context.startActivity(browserIntent)
                } catch (e: ActivityNotFoundException) {
                    browserIntent.setDataAndType(
                        Uri.parse("http://docs.google.com/viewer?url=$inVoiceUrl"),
                        "text/html"
                    )
                    context.startActivity(browserIntent)
                }
            }
        }
    }
}