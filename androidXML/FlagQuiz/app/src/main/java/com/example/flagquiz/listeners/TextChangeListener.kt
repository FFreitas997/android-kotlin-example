package com.example.flagquiz.listeners

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import com.example.flagquiz.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TextChangeListener(val activity: MainActivity, val onTextChange: (value: Editable?) -> Unit) : TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(editable: Editable?) {
        activity
            .lifecycleScope
            .launch {
                delay(300)
                onTextChange(editable)
            }
    }
}