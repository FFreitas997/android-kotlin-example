package com.ffreitas.flowify.utils

import android.app.Dialog
import android.content.Context
import com.ffreitas.flowify.R

class ProgressDialog(context: Context)  {

    private var dialog: Dialog = Dialog(context)

    init {
        dialog.setContentView(R.layout.progress_dialog)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun dismiss() { dialog.dismiss() }

    fun show() { dialog.show() }
}