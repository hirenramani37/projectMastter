package com.massttr.user.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.massttr.user.R
import com.massttr.user.databinding.CustomSpinnerBinding
import java.util.*

class SpinnerArrayAdapter(
    private val con: Context,
    private val layoutId: Int,
    private val stringList: ArrayList<String>,
    private val isPromptedText: Boolean,
) :
    ArrayAdapter<String>(con, layoutId, stringList) {
    override fun isEnabled(position: Int): Boolean {
        return if (isPromptedText) position != 0 else true
    }

    private fun getCustomView(
        position: Int,
        parent: ViewGroup,
    ): View {
        val binding: CustomSpinnerBinding =
            DataBindingUtil.inflate(LayoutInflater.from(con), layoutId, parent, false)

        if (isPromptedText && position == 0)
            binding.tvItemText.setTextAppearance(R.style.spinnerGray)
        else
            binding.tvItemText.setTextAppearance(R.style.spinnerBlack)

        binding.tvItemText.text = stringList[position].capitalize(Locale.getDefault())
        return binding.root
    }

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup,
    ): View {
        super.getDropDownView(position, convertView, parent)
        return getCustomView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }
}


fun Spinner.setUpSpinner(spinnerDataList: ArrayList<String>, promptText: String = "",) {
    this.run {
        if (promptText.isNotEmpty()) {
            spinnerDataList.add(0, promptText)
        }
        val spinnerListAdapter = SpinnerArrayAdapter(
            context, R.layout.custom_spinner,
            spinnerDataList, promptText.isNotEmpty()
        )
        this.adapter = spinnerListAdapter
    }
}

fun Spinner.isValid(isPromptedText: Boolean = false): Boolean {
    return if (isPromptedText) selectedItemPosition != 0 else true
}