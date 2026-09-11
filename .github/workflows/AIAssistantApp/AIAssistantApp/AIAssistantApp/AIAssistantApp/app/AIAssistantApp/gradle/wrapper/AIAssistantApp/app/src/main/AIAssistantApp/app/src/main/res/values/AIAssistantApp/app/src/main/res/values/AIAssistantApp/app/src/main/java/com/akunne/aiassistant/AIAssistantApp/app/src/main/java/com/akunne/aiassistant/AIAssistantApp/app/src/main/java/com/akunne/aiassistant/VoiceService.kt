package com.akunne.aiassistant

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import java.util.Locale

class VoiceService(private val context: Context) {

    private val tts by lazy {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                setupTTS()
            }
        }
    }

    private fun setupTTS() {
        tts.language = Locale.UK
        val voices = tts.voices ?: return
        val maleVoice = voices.find { voice ->
            voice.locale.country == "GB" && voice.name.lowercase().contains("male")
        } ?: voices.find { it.locale == Locale.UK }
        
        if (maleVoice != null) {
            tts.voice = maleVoice
        }
        tts.setPitch(0.9f)
        tts.setSpeechRate(1.0f)
    }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }

    fun startListening(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK.language)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        launcher.launch(intent)
    }

    fun shutdown() {
        if (tts.isSpeaking) tts.stop()
        tts.shutdown()
    }
}
