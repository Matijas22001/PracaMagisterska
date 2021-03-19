package com.example.myapplication.view_binding

interface BaseView {
    fun showMessage(resId: Int)
    fun showMessage(message: String?)
}