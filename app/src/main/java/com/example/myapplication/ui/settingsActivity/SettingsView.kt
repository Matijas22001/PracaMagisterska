package com.example.myapplication.ui.settingsActivity

interface SettingsView {
    fun showMessage(resId: Int)
    fun showMessage(message: String?)
}