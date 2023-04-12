package com.massttr.user.utils

import android.content.Context
import java.lang.ref.WeakReference

object BaseUtils {

    private const val ERROR_INIT = "Initialize BaseUtils with invoke init()"

    private var mWeakReferenceContext: WeakReference<Context>? = null

    val context: Context
        get() {
            requireNotNull(mWeakReferenceContext) { ERROR_INIT }
            return mWeakReferenceContext!!.get()?.applicationContext!!
        }

    /**
     * init in Application
     */
    fun init(ctx: Context) {
        mWeakReferenceContext = WeakReference(ctx)
        //something to do...
    }
}
