package pl.polsl.MathHelper.ui.mainActivity

import androidx.appcompat.app.AlertDialog
import pl.polsl.MathHelper.model.LoginResponse
import pl.polsl.MathHelper.model.SvgImage
import pl.polsl.MathHelper.model.SvgImageDescription
import pl.polsl.MathHelper.model.Tests

interface MainActivityView {
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