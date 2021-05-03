package com.example.myapplication.ui.userListActivity

import androidx.appcompat.app.AlertDialog
import com.example.myapplication.model.LoginResponse
import com.example.myapplication.model.SvgImage
import com.example.myapplication.model.SvgImageDescription
import com.example.myapplication.model.Tests

interface UserListActivityView {
    fun showMessage(resId: Int)
    fun showMessage(message: String?)
    fun updateRecyclerView()
    fun userLoggedInSuccessfulLogic(loginResponse: LoginResponse, dialog: AlertDialog)
    fun userLoggedInFailedLogic()
    fun addElementToList(userId: Int?, imageIdsList: ArrayList<Int>?)
    fun addImageToList(image: SvgImage?)
    fun addImageDescriptionToList(image: SvgImageDescription?)
    fun addTestToList(imageId: Int?, tests: Tests?)
}