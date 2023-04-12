package com.massttr.user.ui.helper

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.massttr.user.R
import com.massttr.user.databinding.TaskDetailsDialogBinding

class DialogHelper {
    companion object {
        fun showOrderDialog(
            context: Context,
            onChange: () -> Unit
        ): Dialog {
            val dialog = Dialog(context)
            val binding: TaskDetailsDialogBinding =
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.task_details_dialog,
                    null,
                    false
                )

           /* binding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                onChange
            }*/

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(binding.root)
            dialog.setCancelable(false)
            dialog.show()
            return dialog
        }
    }
}