package com.ffreitas.flowify.utils

import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import com.ffreitas.flowify.R

class BackPressedCallback(private val context: Activity) : OnBackPressedCallback(true),
    OnClickListener {

    private val dialog: Dialog = Dialog(context).apply {

        setContentView(R.layout.custom_alert_dialog)

        setCanceledOnTouchOutside(false)

        findViewById<Button>(R.id.tv_yes)
            .setOnClickListener(this@BackPressedCallback)

        findViewById<Button>(R.id.tv_no)
            .setOnClickListener(this@BackPressedCallback)
    }

    override fun handleOnBackPressed() {
        Log.w(TAG, "The back button was pressed and handled.")
        dialog.show()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_yes -> {
                Log.w(TAG, "The activity was finished.")
                isEnabled = false // Prevent further back presses while finishing
                context.finish()
            }

            R.id.tv_no -> dialog.dismiss()
        }
    }

    companion object {
        private const val TAG = "BackPressedCallback"
    }
}