package com.example.a7minutesworkout.util

import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.a7minutesworkout.ExerciseActivity
import java.util.Locale

class TextSpeech : TextToSpeech.OnInitListener {

    private val tag = TextSpeech::class.java.simpleName
    private var textToSpeech: TextToSpeech? = null

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            Log.e(tag, "Failed to initialize TextToSpeech")
            return
        }
        val result = textToSpeech?.setLanguage(Locale.US)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            Log.e(tag, "Language not supported")
    }

    fun speakOut(text: String) {
        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun shutdown() {
        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    companion object {

        fun create(context: ExerciseActivity): TextSpeech {
            val instance = TextSpeech()
            instance.textToSpeech = TextToSpeech(context, instance)
            return instance
        }
    }
}