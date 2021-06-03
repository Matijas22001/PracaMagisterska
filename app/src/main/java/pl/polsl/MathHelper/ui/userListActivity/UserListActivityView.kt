package pl.polsl.MathHelper.ui.userListActivity

import pl.polsl.MathHelper.model.StudentListResponse

interface UserListActivityView {
    fun showMessage(resId: Int)
    fun showMessage(message: String?)
    fun updateRecyclerView(studentListResponse: StudentListResponse?)
}