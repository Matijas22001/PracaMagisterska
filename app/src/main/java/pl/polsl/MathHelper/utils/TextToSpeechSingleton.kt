package pl.polsl.MathHelper.utils

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import java.util.*
import java.util.concurrent.TimeUnit

class TextToSpeechSingleton(context: Context) {

    private var instance: TextToSpeechSingleton? = null
    private var ctx: Context? = context
    private var mTTS: TextToSpeech? = null
    private var TTSready = false

    init {
        mTTS = TextToSpeech(context) {
            TTSready = true
            configTTS()
        }
    }

    fun setSpeechSpeed(speechSpeed: Float?){
        if (speechSpeed != null) {
            mTTS?.setSpeechRate(speechSpeed)
        }
    }

    @Synchronized
    fun getInstance(context: Context): TextToSpeechSingleton? {
        if (instance == null) {
            instance = TextToSpeechSingleton(context)
        }
        return instance
    }

    fun isTTSready(): Boolean {
        return TTSready
    }

    private fun configTTS() {
        val available = mTTS!!.isLanguageAvailable(Locale.getDefault())
        if (available != TextToSpeech.LANG_MISSING_DATA
                && available != TextToSpeech.LANG_NOT_SUPPORTED) {
            mTTS?.language = Locale(Locale.getDefault().language)
        } else {
            /** TODO SAVE  */
        }
    }

    fun speakSentence(sentence: String?) {
        if (TTSready) {
            mTTS?.setSpeechRate(AppPreferences.speechSpeed)
            mTTS?.speak(sentence, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    fun speakSentence(sentence: String?, speechSpeed: Float?) {
        if (TTSready) {
            mTTS?.setSpeechRate(speechSpeed!!)
            mTTS?.speak(sentence, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    fun speakSentenceWithoutDisturbing(sentence: String?) {
        mTTS?.setSpeechRate(AppPreferences.speechSpeed)
        mTTS?.speak(sentence, TextToSpeech.QUEUE_ADD, null)
    }

    fun sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(50)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}