package com.massttr.user.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.common.base.BaseAdapter
import com.github.nikulvaghani.NikNotify
import com.massttr.provider.BuildConfig
import com.massttr.provider.MyApp
import com.massttr.provider.R
import com.massttr.provider.chat.FixerSocketService
import com.massttr.provider.databinding.ToastLayoutBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.anko.alert
import org.jetbrains.anko.startService
import org.jetbrains.anko.toast
import timber.log.Timber
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
    return (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }
}

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
fun Context.startSocketService() {
    if (MyApp.getInstance().getPref().isLogin == false) return

    if (isServiceRunning(FixerSocketService::class.java) && FixerSocketService.getSocketInstance() != null) {
        Timber.d("running")
        FixerSocketService.getSocketInstance()?.connect()
    } else {
        Timber.d("started")
        startService<FixerSocketService>()
    }
}

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
fun Context.stopSocketService() {
    if (MyApp.getInstance().getPref().isLogin == false) return

    if (isServiceRunning(FixerSocketService::class.java) && FixerSocketService.getSocketInstance() != null) {
        Timber.d("running")
        FixerSocketService.getSocketInstance()?.disconnect()
    } else {
//        as of service is not running we are going to disconnect connection i.e check onDestroy of SocketService
        Timber.d("might be SocketService is not exist so connection will not in use")
    }
}


fun View.closeSoftKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun getNameFromUrl(url: String?): String? {
    return Uri.parse(url).lastPathSegment
}

fun getSaveDir(): String? {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        .toString() + "/Fixer"
}

fun RecyclerView.setAdapter(adapter: RecyclerView.Adapter<*>?, emptyView: View?) {
    val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty(emptyView)
            super.onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty(emptyView)
            super.onItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty(emptyView)
            super.onItemRangeRemoved(positionStart, itemCount)
        }
    }

    this.adapter?.unregisterAdapterDataObserver(observer)
    this.adapter = adapter
    if (adapter is BaseAdapter<*, *>) {
        adapter.setEmptyView(emptyView)
    }
    adapter?.registerAdapterDataObserver(observer)
    checkIfEmpty(emptyView)
}

fun RecyclerView.checkIfEmpty(emptyView: View?) {
    if (emptyView != null && adapter != null) {
        val emptyViewVisible = adapter?.itemCount == 0
        emptyView.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
        visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
    }
}

private fun RecyclerView.checkIfEmpty(emptyView: View?, goneView: View?) {
    if (emptyView != null && adapter != null) {
        val emptyViewVisible = adapter?.itemCount == 0
        emptyView.isVisible = emptyViewVisible
        goneView?.isVisible = false
        isVisible = !emptyViewVisible
    }
}

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

fun Context.copy(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip: ClipData = ClipData.newPlainText("Copy", text)
    clipboard?.setPrimaryClip(clip)
}


fun Context.share(text: String, shareFile: File) {
    try {
        if (!shareFile.exists()) {
            toast("File not exists...")
            return
        }

        val intent = Intent(Intent.ACTION_SEND)
        shareFile.let {
            intent.type = "*/*"  /*video/mp4  WA*/
            val uri = FileProvider.getUriForFile(
                this, applicationContext.packageName + ".provider",
                it
            )
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_TEXT, text)
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.isNetworkConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.isBlank(): Boolean {
    return this.text.toString().isBlank()
}

fun Context.loadImagesGlide(data: Any, img: ImageView, placeholder: Int) {
    Glide.with(this)
        .load(data)
        .placeholder(placeholder)
        .error(placeholder)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(img)
}

fun String.toHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    }
}

fun File.multipartImageBody(paramName: String): MultipartBody.Builder {
    val builder = MultipartBody.Builder()
    builder.setType(MultipartBody.FORM)
    builder.addFormDataPart(
        paramName, name.replace("-", "_"), asRequestBody("image/*".toMediaTypeOrNull())
    )
    return builder
}

fun TextView.makeLinks(
    @ColorInt linkColor: Int = 0,
    vararg links: Pair<String, ((view: View) -> Unit)>,
) {
    try {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {

                override fun updateDrawState(textPaint: TextPaint) {
                    // use this to change the link color
                    if (linkColor == 0)
                        textPaint.color = textPaint.linkColor
                    else textPaint.color = linkColor
                    // toggle below value to enable/disable
                    // the underline shown below the clickable text
                    textPaint.isUnderlineText = true
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first, ignoreCase = true)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun String.showErrorToast() {
    this.showToast(ToastType.TOAST_ERROR)
}

fun String.showWaringToast() {
    this.showToast(ToastType.TOAST_WARNING)
}

fun String.showSuccessToast() {
    this.showToast(ToastType.TOAST_SUCCESS)
}

enum class ToastType {
    TOAST_SUCCESS,
    TOAST_ERROR,
    TOAST_WARNING
}

private var toast: Toast? = null
private fun String.showToast(toastType: ToastType) {
    val inflater = LayoutInflater.from(MyApp.getInstance().applicationContext)
    val layoutView: ToastLayoutBinding =
        DataBindingUtil.inflate(inflater, R.layout.toast_layout, null, false)
    when (toastType) {
        ToastType.TOAST_SUCCESS -> {
            layoutView.tvToastMsg.backgroundTintList =
                ContextCompat.getColorStateList(
                    MyApp.getInstance().applicationContext,
                    R.color.colorToastSuccess
                )
        }
        ToastType.TOAST_ERROR -> {
            layoutView.tvToastMsg.backgroundTintList =
                ContextCompat.getColorStateList(
                    MyApp.getInstance().applicationContext,
                    R.color.colorToastError
                )
        }
        ToastType.TOAST_WARNING -> {
            layoutView.tvToastMsg.backgroundTintList =
                ContextCompat.getColorStateList(
                    MyApp.getInstance().applicationContext,
                    R.color.colorToastWarning
                )
        }
    }

    layoutView.tvToastMsg.text = this

    if (toast != null)
        toast?.cancel()

    toast = Toast(MyApp.getInstance().applicationContext)
    toast?.setGravity(Gravity.BOTTOM, 0, 100)
    toast?.duration = Toast.LENGTH_SHORT
    toast?.view = layoutView.root
    toast?.show()
}

fun View.gone() {
    if (visibility != View.GONE)
        visibility = View.GONE
}

fun View.visible() {
    if (visibility != View.VISIBLE)
        visibility = View.VISIBLE
}

fun Context.loadImages(data: Any, img: ImageView) {
    Glide.with(this)
        .load(data)
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_placeholder)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(img)
}

//todo use this method to set Online or Offline status in header
//fun SharedPref.setOnOfflineStatus(
//    context: Context,
//    imageView: ImageView,
//    textView: TextView
//) {
//    if (this.isOnline == true) {
//        imageView.setImageResource(R.drawable.ic_online)
//        textView.text = context.getString(R.string.online)
//    } else {
//        imageView.setImageResource(R.drawable.ic_offline)
//        textView.text = context.getString(R.string.offline)
//    }
//}

fun String.getSystemDateStringFromUTCDate(outFormat: String): String {
    if (isNullOrEmpty()) return ""
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val utcDate = formatter.parse(split("\\.")[0].replace("T", " "))

        val dateFormatter =
            SimpleDateFormat(outFormat, Locale.getDefault()) //this format changeable
        dateFormatter.timeZone = TimeZone.getDefault()
        dateFormatter.format(utcDate)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun String.getSystemDateCalendarFromUTCDate(): Calendar {
    if (isNullOrEmpty()) return Calendar.getInstance()
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val utcDate = formatter.parse(split("\\.")[0].replace("T", " ")) ?: Date()

        val dateFormatter =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) //this format changeable
        dateFormatter.timeZone = TimeZone.getDefault()

        val calendar = Calendar.getInstance()
        calendar.time = dateFormatter.parse(dateFormatter.format(utcDate)) ?: Date()
        calendar
    } catch (e: Exception) {
        e.printStackTrace()
        Calendar.getInstance()
    }
}

fun getMessageTime(utcDateString: String?, outFormat: String = FORMAT_MESSAGE_TIME): String {
    return utcDateString?.getSystemDateStringFromUTCDate(outFormat) ?: ""
}

fun View.captureView(window: Window?, bitmapCallback: (Bitmap) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Above Android O, use PixelCopy
        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        val location = IntArray(2)
        getLocationInWindow(location)
        window?.let {
            PixelCopy.request(
                it,
                Rect(location[0], location[1], location[0] + width, location[1] + height),
                bitmap,
                {
                    if (it == PixelCopy.SUCCESS) {
                        bitmapCallback.invoke(bitmap)
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }
    } else {
        val tBitmap = Bitmap.createBitmap(
            measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(tBitmap)
        layout(0, 0, measuredWidth, measuredHeight)
        draw(canvas)
        canvas.setBitmap(null)
        bitmapCallback.invoke(tBitmap)
    }
}


fun Context.showDeniedPermissionDialog(
    message: String,
    positiveClick: () -> Unit,
    negativeClick: () -> Unit,
) {
    alert(message) {
        positiveButton("Allow") {
            startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            })
            positiveClick()
        }
        negativeButton("Deny") {
            negativeClick()
        }
    }.show()
}


fun Context.showAskingPermissionDialog(
    message: String,
    positiveClick: () -> Unit,
    negativeClick: () -> Unit,
) {
    val alert = alert(message) {
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

fun Activity.setShakeError(
    message: String,
    radius: Int = 10,
    marginLeft: Int = 7,
    marginTop: Int = 10,
    marginRight: Int = 7,
    marginBottom: Int = 0
) {
//    val typeface = ResourcesCompat.getFont(this, R.font.gotham_bold)
//    typeface?.let {
//        Sneaker.with(this)
//            .setIcon(R.drawable.ic_error, R.color.colorWhite, false)
//            .setTitle(message, R.color.colorWhite)
//            .setTypeface(it)
//            .setCornerRadius(radius, marginLeft, marginTop, marginRight, marginBottom)
//            .sneak(R.color.colorToastError)
//    }
    NikNotify.with(this)
        .setMessage(message)
        .error()
        .setDismissTime(2000)
        .build()
}

fun Activity.setShakeSuccess(
    message: String,
    radius: Int = 10,
    marginLeft: Int = 20,
    marginTop: Int = 10,
    marginRight: Int = 7,
    marginBottom: Int = 0
) {
//    val typeface = ResourcesCompat.getFont(this, R.font.gotham_bold)
//    typeface?.let {
//        Sneaker.with(this)
//            .setIcon(R.drawable.ic_check_bg, false)
//            .setTitle(message, R.color.colorWhite)
//            .setTypeface(it)
//
//            .setCornerRadius(radius, marginLeft, marginTop, marginRight, marginBottom)
//            .sneak(R.color.colorToastSuccess)
//    }

    NikNotify.with(this)
        .setMessage(message)
        .success()
        .build()
}

fun Activity.setShakeErrorInternet(
    message: String,
    radius: Int = 10,
    marginLeft: Int = 7,
    marginTop: Int = 10,
    marginRight: Int = 7,
    marginBottom: Int = 0
) {
//    val typeface = ResourcesCompat.getFont(this, R.font.gotham_bold)
//    typeface?.let {
//        Sneaker.with(this)
//            .setIcon(R.drawable.ic_error, R.color.colorWhite, false)
//            .setTitle(message, R.color.colorWhite)
//            .setTypeface(it)
//            .setCornerRadius(radius, marginLeft, marginTop, marginRight, marginBottom)
//            .sneak(R.color.colorBlack)
//    }

    NikNotify.with(this)
        .setMessage(message)
        .error()
        .setBackgroundColor(intArrayOf(R.color.colorBlack))
        .build()
}

//fun View.setShakeError(errorMsg: String) {
//    if (errorMsg.isNotEmpty()) {
//        errorMsg.showToast(ToastType.TOAST_ERROR)
//    }
//    this.shake()
//}


//after remove
fun View.shake() {
    this.startAnimation(
        AnimationUtils.loadAnimation(
            MyApp.getInstance().applicationContext,
            R.anim.shake
        )
    )
}

fun TextView.setDrawableColor(@ColorRes color: Int) {
    this.compoundDrawables.forEach {
        it?.let {
            it.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, color),
                PorterDuff.Mode.SRC_IN
            )
        }
    }
}

fun changeDateFormat(Data: String, CurrentFormat: String, ChangeFormat: String): String {
    var change: String
    var sdf = SimpleDateFormat(CurrentFormat, Locale.getDefault())
    val date1: Date?
    try {
        date1 = sdf.parse(Data)
        sdf = SimpleDateFormat(ChangeFormat, Locale.getDefault())
        change = sdf.format(date1)
    } catch (e: ParseException) {
        e.printStackTrace()
        change = ""
    }
    return change
}

fun String.isEmailValid(): Boolean {
    val pattern: Pattern
    val emailPattern =
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    pattern = Pattern.compile(emailPattern)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String.toDate(): Date? {
    val date = this.split("-")
    val calender = Calendar.getInstance()
    calender.set(Calendar.DAY_OF_MONTH, date.last().trim().toInt())
    calender.set(Calendar.MONTH, date[1].trim().toInt() - 1)
    calender.set(Calendar.YEAR, date.first().trim().toInt())
    return calender.time
}

fun Context.getAppDirectory(): File {
// var appDirectory = getExternalFilesDir(null)
    val appDirectory = Environment.getExternalStorageDirectory()
    val file = File(appDirectory?.absolutePath + "/" + getString(R.string.app_name))
    if (!file.exists()) file.mkdirs()
    return file
}

fun EditText.getTextString(): String {
    return this.text.toString().trim()
}

val String.isMobileNumberValid: Boolean
    get() {
        val pattern: Pattern = Pattern.compile("07[5|7|8|9][0-9]{8}")
        val matcher: Matcher = pattern.matcher(this)
        return matcher.matches()
    }

fun Context.gmail(email: String) {
    val gmail = Intent(Intent.ACTION_SENDTO)
    gmail.data = Uri.parse("mailto:")
    gmail.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    gmail.putExtra(Intent.EXTRA_SUBJECT, "Subject")
    gmail.putExtra(Intent.EXTRA_TEXT, "Compose email")
    startActivity(gmail)
}

fun Context.call(number: String) {
    val call = Intent(Intent.ACTION_DIAL)
    call.data = Uri.parse("tel:$number")
    startActivity(call)
}

fun ImageView.loadChatMediaImage(
    imgSrc: String,
    progressBar: ProgressBar? = null,
    placeholder: Int = R.drawable.img_placeholder
) {
    progressBar?.visible()
    Glide.with(context)
        .load(imgSrc)
        .placeholder(placeholder)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(placeholder)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                Timber.i("Loading Image $e")
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }

        })
        .into(this)
}

fun timeToConUTC(time: String): String {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    df.timeZone = TimeZone.getTimeZone("UTC")
    val date = df.parse(time)
    df.timeZone = TimeZone.getDefault()
    return df.format(date)
}