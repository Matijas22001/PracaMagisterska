package com.example.myapplication.ui.userListActivity

interface UserListActivityView {
    fun showMessage(resId: Int)
    fun showMessage(message: String?)
    fun updateRecyclerView()
}