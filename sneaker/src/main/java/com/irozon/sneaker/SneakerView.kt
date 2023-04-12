package com.irozon.sneaker

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.irozon.sneaker.Utils.customView
import com.irozon.sneaker.widget.CircularImageView

internal class SneakerView(context: Context?) : LinearLayout(context) {
    init {
        id = R.id.mainLayout
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private val DEFAULT_VALUE = -100000

    fun setIcon(icon: Drawable?, isCircular: Boolean, iconSize: Int, colorFilter: Int) {
        icon?.let {
            val ivIcon =
                if (!isCircular) AppCompatImageView(context)
                else CircularImageView(context)
            val layoutParams = LayoutParams(iconSize, iconSize)
            val marginLeft = Utils.convertDpToPixel(context, 12f).toInt()
            layoutParams.setMargins(marginLeft, 0, 0, 0)
            ivIcon.layoutParams = layoutParams
            ivIcon.setImageDrawable(it)
            ivIcon.isClickable = false
            if (colorFilter != DEFAULT_VALUE) ivIcon.setColorFilter(colorFilter)
            addView(ivIcon, 0)
        }
    }

    fun setTextContent(
        title: String,
        titleColor: Int,
        description: String,
        messageColor: Int,
        typeface: Typeface?
    ) {
        // Title and description
        val textLayout = LinearLayout(context)
        val textLayoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textLayout.layoutParams = textLayoutParams
        textLayout.orientation = VERTICAL

        // Title
        if (title.isNotEmpty()) {
            val tvTitle = TextView(context)

            tvTitle.layoutParams = textLayoutParams
            tvTitle.gravity = Gravity.CENTER_VERTICAL
            tvTitle.textSize = Utils.convertDpToPixel(context, 8F)
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utils.convertDpToPixel(context, 10f))
            tvTitle.text = title
            tvTitle.isClickable = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tvTitle.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            }
            tvTitle.setPadding(
                Utils.convertDpToPixel(context, 8F).toInt(),
                if (description.isNotEmpty()) Utils.convertDpToPixel(context, 8F).toInt() else 0,
                Utils.convertDpToPixel(context, 8F).toInt(),
                0
            ) // Top padding only if there is message
            if (titleColor != DEFAULT_VALUE) tvTitle.setTextColor(titleColor)
            if (typeface != null) tvTitle.typeface = typeface

            textLayout.addView(tvTitle)
        }

        // Description
        if (description.isNotEmpty()) {
            val tvMessage = TextView(context)
            tvMessage.layoutParams = textLayoutParams
            tvMessage.gravity = Gravity.CENTER_VERTICAL
            tvMessage.textSize = Utils.convertDpToPixel(context, 4f)
//            tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utils.convertDpToPixel(context, 7f))
            tvMessage.text = description
            tvMessage.isClickable = false

            tvMessage.setPadding(
                Utils.convertDpToPixel(context, 12f).toInt(),
                0,
                Utils.convertDpToPixel(context, 12f).toInt(),
                if (title.isNotEmpty()) Utils.convertDpToPixel(context, 12f).toInt() else 0
            ) // Top padding only if there is message
            if (messageColor != DEFAULT_VALUE) tvMessage.setTextColor(messageColor)
            if (typeface != null) tvMessage.typeface = typeface

            textLayout.addView(tvMessage)
        }
        addView(textLayout)
    }

    fun setBackground(colors: IntArray, cornerRadius: Int) {
        if (cornerRadius == DEFAULT_VALUE)
            setBackgroundColor(colors[0])
        else
            background = customView(context, colors, cornerRadius)
    }

    fun setBackground(color: Int, cornerRadius: Int) {
        if (cornerRadius == DEFAULT_VALUE) setBackgroundColor(color)
        else background = customView(context, color, cornerRadius)
    }

    fun setCustomView(view: View) {
        addView(view, 0)
    }
}