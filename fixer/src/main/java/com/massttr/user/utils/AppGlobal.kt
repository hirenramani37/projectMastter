package com.massttr.user.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.format.DateUtils
import android.view.Window
import androidx.core.content.ContextCompat
import com.common.data.prefs.SharedPref
import com.google.firebase.messaging.FirebaseMessaging
import com.massttr.provider.BuildConfig
import com.massttr.provider.MyApp
import com.massttr.provider.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class AppGlobal {
    companion object {
        private val pref: SharedPref by lazy { MyApp.getInstance().getPref() }
        fun setStatusBarGradiant(context: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = context.window
                val background: Drawable? =
                    ContextCompat.getDrawable(context, R.drawable.bg_gradient_button)
                //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
//                window.navigationBarColor = ContextCompat.getColor(context, android.R.color.transparent)
                window.setBackgroundDrawable(background)
            }
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

        fun getFcmToken(activity: Activity, onFcmToken: (token: String) -> Unit) {
            if (pref.fcmToken?.trim()?.isEmpty() == true)
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    pref.fcmToken = it
                    onFcmToken(pref.fcmToken!!)
                }.addOnFailureListener {
                    activity.setShakeError(R.string.something_went_wrong.toString())
                    Timber.e(it)
                }
            else
                onFcmToken(pref.fcmToken.toString())
        }

        fun alertDialog(context: Context?, message: String, vararg title: String) {
            if (context == null) return
            if (message.isEmpty()) return
            val displayTitle =
                if (title.isEmpty()) context.getString(R.string.app_name)
                else title[0]
            context.alert(message, displayTitle) { okButton { it.dismiss() } }.show()
//            try {
//                context?.let { context ->
//                    val dialog = Dialog(context)
//                    dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
//                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                    dialog.setContentView(R.layout.dialog_alert)
//                    dialog.setCanceledOnTouchOutside(true)
//                    dialog.tvMessage.text = message
//                    dialog.tvCancel.visibility = View.GONE
//                    dialog.tvCancel.setOnClickListener { dialog.dismiss() }
//
//                    dialog.tvOk.setOnClickListener {
//                        dialog.dismiss()
//                        if (type.isNotEmpty() && type[0] == N_TYPE_REFFERAL) {
//                            context.startActivity<MyRefferalsActivity>()
//                        } else if (type.isNotEmpty() && type[0] == ALERT_COMPLETE_PROFILE) {
//                            val profile = HaochiApp.getInstance().getAppPreferencesHelper().getUserProfile()
//                            context.startActivity<EditProfileActivity>(AppConstant.ARG_PROFILE_DATA to profile)
//                        }
//                    }
//                    loadNativeAds(dialog.flNativeAd)
//                    dialog.show()
//                }
//            } catch (e: Exception) {
//            }
        }

         fun getChatHeaderId(utcDateStr: String?): Long {
            if (utcDateStr.isNullOrEmpty()) return 0L

            val sysDateStr = utcDateStr.getSystemDateStringFromUTCDate(BuildConfig.UtcFormat)
            val date =
                SimpleDateFormat(BuildConfig.UtcFormat, Locale.getDefault()).parse(sysDateStr) ?: Date()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val onlyDateString = sdf.format(date)
            val onlyDate = sdf.parse(onlyDateString) ?: Date()

            return onlyDate.time
        }


         fun getChatHeaderValue(utcDateStr: String?): String {
            if (utcDateStr.isNullOrEmpty()) return ""

            val sysDateStr = utcDateStr.getSystemDateStringFromUTCDate(BuildConfig.UtcFormat)
            val sysDate =
                SimpleDateFormat(BuildConfig.UtcFormat, Locale.getDefault()).parse(sysDateStr) ?: Date()

            val currentDate = Date()
            val diff = currentDate.time - sysDate.time
            val totalHours = diff / (1000 * 60 * 60)

            val currentCal = Calendar.getInstance()
            val lastSeenDateCal = Calendar.getInstance()
            lastSeenDateCal.time = sysDate

            val currentSysDate = currentCal.get(Calendar.DATE)
            val lastSeenDate = lastSeenDateCal.get(Calendar.DATE)

            return if (totalHours < 24 && lastSeenDate == currentSysDate) {
                "Today"
            } else if (DateUtils.isToday(sysDate.time + DateUtils.DAY_IN_MILLIS)) {
                "Yesterday"
            } else {
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                sdf.format(sysDate)
            }
        }


        fun convertToArabic(value: Any): String {
            return if (pref.isArabic == true) (value.toString() + "")
                .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
                .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
                .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
                .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
                .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
            else value.toString()
        }


        fun convertToEnglish(value: Any): String {
            return if (pref.isArabic == true) (value.toString() + "")
                .replace("١".toRegex(), "1").replace("٢".toRegex(), "2")
                .replace("٣".toRegex(), "3").replace("٤".toRegex(), "4")
                .replace("٥".toRegex(), "5").replace("٦".toRegex(), "6")
                .replace("٧".toRegex(), "7").replace("٨".toRegex(), "8")
                .replace("٩".toRegex(), "9").replace("٠".toRegex(), "0")
            else value.toString()
        }
    }


}




