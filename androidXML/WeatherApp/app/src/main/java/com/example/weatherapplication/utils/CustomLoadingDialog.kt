package com.example.weatherapplication.utils

import android.app.Dialog
import android.content.Context
import com.example.weatherapplication.R

class CustomLoadingDialog(private val context: Context) {

    private var dialog: Dialog? = null

    init { builder() }

    private fun builder() {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.custom_progress_dialog)
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun dismiss() { (dialog ?: return).dismiss() }

    fun show() { (dialog ?: return).show() }
}