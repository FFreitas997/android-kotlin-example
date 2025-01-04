package com.ffreitas.flowify.utils

import android.app.Dialog
import android.content.Context
import android.util.Log
import com.ffreitas.flowify.R

class ProgressDialog(context: Context) {

    private var dialog: Dialog = Dialog(context)

    init {
        dialog.setContentView(R.layout.progress_dialog)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun dismiss() {
        Log.w(TAG, "The progress dialog was dismissed.")
        dialog.dismiss()
    }

    fun show() {
        Log.w(TAG, "The progress dialog was shown.")
        dialog.show()
    }

    companion object {
        private const val TAG = "ProgressDialog"
    }
}