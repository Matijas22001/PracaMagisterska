package com.example.myapplication.utils

import android.view.View
import android.view.Window
import com.example.myapplication.model.Flags

class ViewUtils {
    companion object{
        fun fullScreenCall(window: Window) {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
            window.decorView.systemUiVisibility = Flags.flags
        }
    }
}