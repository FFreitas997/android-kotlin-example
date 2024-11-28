package com.example.a7minutesworkout.util

import android.app.Dialog
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.example.a7minutesworkout.ExerciseActivity
import com.example.a7minutesworkout.databinding.DialogCustomBackConfirmationBinding

class BackPressedCallback(val context: ExerciseActivity) : OnBackPressedCallback(true) {

    private val tag = BackPressedCallback::class.java.simpleName
    private val dialog: Dialog

    init { dialog = createCustomDialog() }

    override fun handleOnBackPressed() {
        Log.w(tag, "The back button was pressed and handled.")
        dialog.show()
    }

    private fun createCustomDialog(): Dialog {
        val warningCustomDialog = Dialog(context)

        val dialogBinding = DialogCustomBackConfirmationBinding
            .inflate(context.layoutInflater)
            .also { warningCustomDialog.setContentView(it.root) }

        warningCustomDialog.setCanceledOnTouchOutside(false)

        dialogBinding
            .tvYes
            .setOnClickListener { warningCustomDialog.dismiss(); context.finish() }

        dialogBinding
            .tvNo
            .setOnClickListener { warningCustomDialog.dismiss() }

        return warningCustomDialog
    }

}