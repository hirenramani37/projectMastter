package com.irozon.sneaker

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.core.content.ContextCompat


/**
 * Created by hammad.akram on 2/27/18.
 */

internal object Utils {

    /**
     * Returns status bar height.
     *
     * @return
     */
    fun getStatusBarHeight(activityDecorView: ViewGroup?): Int {
        return if (activityDecorView != null) {
            val rectangle = Rect()
            activityDecorView.getWindowVisibleDisplayFrame(rectangle)
            rectangle.top
        } else {
            0
        }
    }

    /*fun convertToDp(context: Context, sizeInDp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (sizeInDp * density + 0.5f).toInt()
    }*/

    fun convertDpToPixel(context: Context, dp: Float): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /*fun convertPixelsToDp(context: Context, px: Float): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }*/

    fun customView(context: Context, backgroundColor: Int, cornerRadius: Int): GradientDrawable {
        val radiusInDP = convertDpToPixel(context, cornerRadius.toFloat())
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(
                radiusInDP,
                radiusInDP,
                radiusInDP,
                radiusInDP,
                radiusInDP,
                radiusInDP,
                radiusInDP,
                radiusInDP
            )
            setColor(backgroundColor)
        }
    }

    fun customView(
        context: Context,
        backgroundColors: IntArray,
        cornerRadius: Int
    ): GradientDrawable {
        val radiusInDP = convertDpToPixel(context, cornerRadius.toFloat())
        return GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, backgroundColors).apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(
                radiusInDP,
                radiusInDP,
                radiusInDP,
                radiusInDP,
                radiusInDP,
                radiusInDP,
                radiusInDP,
                radiusInDP
            )
//            setColor(backgroundColor)
        }
    }

    fun getColor(context: Context, color: Int): Int {
        return try {
            ContextCompat.getColor(context, color)
        } catch (e: Exception) {
            color
        }
    }
}
