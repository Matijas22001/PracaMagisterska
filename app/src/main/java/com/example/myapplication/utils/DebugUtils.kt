package com.example.myapplication.utils

import com.example.myapplication.view_binding.BaseView
import com.example.myapplication.R
import com.orhanobut.hawk.Hawk

object DebugUtils {
    var DEBUG_MODE = "DEBUG_MODE"
    private var i = 0

    val isInDebugMode: Boolean
        get() = Hawk.get(DEBUG_MODE, false)

    private fun startDebugMode(view: BaseView) {
        Hawk.put(DEBUG_MODE, true)
        //view.showMessage(R.string.you_are_developer)
    }


    fun incrementDebugCounter(view: BaseView) {
        i++
        if (i == 9) {
            startDebugMode(view)
        }
    }
}