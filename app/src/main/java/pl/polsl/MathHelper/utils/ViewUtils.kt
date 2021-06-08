package pl.polsl.MathHelper.utils

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import pl.polsl.MathHelper.model.Flags

class ViewUtils {
    companion object{
        fun fullScreenCall(window: Window) {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
            when {
                Build.VERSION.SDK_INT < 16 -> {
                    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
                Build.VERSION.SDK_INT < 30 -> {
                    window.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        window.decorView.windowInsetsController?.hide(WindowInsets.Type.statusBars())
                    }
                }
            }
        }
    }
}