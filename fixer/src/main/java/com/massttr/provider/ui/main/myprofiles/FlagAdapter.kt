package com.massttr.provider.ui.main.myprofiles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.massttr.provider.R

class FlagAdapter(
    var mContext: Context,
    var spinnerImages: Array<Int>,
) :
    ArrayAdapter<String?>(mContext, R.layout.custom_img_spinner) {
    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View = getView(position, convertView, parent)

    override fun getCount(): Int = spinnerImages.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var mViewHolder = ViewHolder()
        if (convertView == null) {
            val mInflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = mInflater.inflate(R.layout.custom_img_spinner, parent, false)
            mViewHolder.mFlag = convertView!!.findViewById<View>(R.id.imgFlag) as ImageView?
            convertView.tag = mViewHolder
        } else {
            mViewHolder = convertView.tag as ViewHolder
        }
        mViewHolder.mFlag!!.setImageResource(spinnerImages[position])
        return convertView
    }

    private class ViewHolder {
        var mFlag: ImageView? = null
    }
}
