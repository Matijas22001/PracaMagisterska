package com.example.myapplication.ui.userListActivity

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.myapplication.model.Student
import com.example.myapplication.model.StudentList
import com.google.gson.Gson

class UserListActivityPresenter(private val view: UserListActivityView?, private val navigator: UserListActivityNavigator?){
    fun getUserListFromServer(queue: RequestQueue, studentListToShow: ArrayList<Student>, token: String){
        val url = "http://157.158.57.124:50820/api/device/Students/GetStudents"
        var studentList: StudentList? = null
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            Response.Listener { response ->
                studentList = Gson().fromJson(response.toString(),StudentList::class.java)
                studentListToShow.addAll(studentList?.studentsList!!)
                view?.updateRecyclerView()
            }, Response.ErrorListener { error ->
                error.stackTrace
            }) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $token"
                return params
            }
        }
        queue.add(jsonObjectRequest)

    }
}