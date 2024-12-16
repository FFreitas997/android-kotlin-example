package com.ffreitas.flowify.utils

import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import com.ffreitas.flowify.R

class BackPressedCallback(private val context: Activity, enabled: Boolean) : OnBackPressedCallback(enabled), OnClickListener {

    private val tag = BackPressedCallback::class.java.simpleName
    private val dialog: Dialog

    init {
        dialog = buildDialog()
    }

    override fun handleOnBackPressed() {
        Log.w(tag, "The back button was pressed and handled.")
        dialog.show()
    }

    private fun buildDialog() =
        Dialog(context)
            .apply {
                setContentView(R.layout.custom_alert_dialog)

                setCanceledOnTouchOutside(false)

                findViewById<Button>(R.id.tv_yes)
                    .setOnClickListener(this@BackPressedCallback)

                findViewById<Button>(R.id.tv_no)
                    .setOnClickListener(this@BackPressedCallback)
            }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_yes -> {
                isEnabled = false
                dialog.dismiss()
                context.finish()
            }

            R.id.tv_no -> dialog.dismiss()
        }
    }
}