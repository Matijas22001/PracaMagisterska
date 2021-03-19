package com.example.myapplication.ui.mainActivity

interface MainActivityView {
    fun showMessage(resId: Int)
    fun showMessage(message: String?)
}