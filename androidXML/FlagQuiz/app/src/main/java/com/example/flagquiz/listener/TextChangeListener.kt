package com.example.flagquiz.listener

import android.text.Editable
import android.text.TextWatcher
import com.example.flagquiz.MainActivity
import java.util.Timer
import java.util.TimerTask


class TextChangeListener(val activity: MainActivity, val onTextChange: (value: Editable?) -> Unit) : TextWatcher {

    private var timer = Timer()

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        timer.cancel()
        timer = Timer()
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    activity.runOnUiThread { onTextChange(p0) }
                }
            }, 600
        )
    }
}