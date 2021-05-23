package com.example.myapplication.ui.userListActivity

import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.example.myapplication.model.*
import com.google.gson.Gson
import org.json.JSONObject

class UserListActivityPresenter(private val view: UserListActivityView?, private val navigator: UserListActivityNavigator?){
    fun loginUser(queue: RequestQueue, login: String, password: String, dialog: AlertDialog){
        val url = "http://157.158.57.124:50820/api/Auth/Login"
        var loginResponse: LoginResponse? = null
        val jsonObjectRequestData = JSONObject()
        jsonObjectRequestData.put("userName", login)
        jsonObjectRequestData.put("password", password)
        jsonObjectRequestData.put("rememberMe", true)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, null,
            { response ->
                loginResponse = Gson().fromJson(response.toString(),LoginResponse::class.java)
                view?.userLoggedInSuccessfulLogic(loginResponse!!, dialog)
            },
            { error ->
                error.stackTrace
            }
        )
        queue.add(jsonObjectRequest)
    }

    fun getUserListFromServer(queue: RequestQueue, studentListToShow: ArrayList<Student>){
        val url = "http://157.158.57.124:50820/api/device/Students/GetStudents"
        var studentList: StudentList? = null
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                studentList = Gson().fromJson(response.toString(),StudentList::class.java)
                studentListToShow.addAll(studentList?.studentsList!!)
                view?.updateRecyclerView()
            },
            { error ->
                error.stackTrace
            }
        )
        queue.add(jsonObjectRequest)
    }

    fun getUserImageIdsFromServer(queue: RequestQueue, studentId: Int){
        val url = "http://157.158.57.124:50820/api/device/Images/GetStudentImageIds"
        val jsonObjectRequestData = JSONObject()
        jsonObjectRequestData.put("studentId", studentId)
        var userImagesResponse: UserImagesResponse? = null
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObjectRequestData,
            { response ->
                userImagesResponse = Gson().fromJson(response.toString(),UserImagesResponse::class.java)
                view?.addElementToList(studentId, userImagesResponse?.svgIdList)
            },
            { error ->
                error.stackTrace
            }
        )
        queue.add(jsonObjectRequest)
    }

    fun getUserImageFromServer(queue: RequestQueue, imageId: Int){
        val url = "http://157.158.57.124:50820/api/device/Images/GetImage"
        val jsonObjectRequestData = JSONObject()
        jsonObjectRequestData.put("imageId", imageId)
        var svgImage: SvgImage? = null
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObjectRequestData,
            { response ->
                svgImage = Gson().fromJson(response.toString(),SvgImage::class.java)
                view?.addImageToList(svgImage)
            },
            { error ->
                error.stackTrace
            }
        )
        queue.add(jsonObjectRequest)
    }

    fun getImageDescriptionFromServer(queue: RequestQueue, imageId: Int){
        val url = "http://157.158.57.124:50820/api/device/Descriptions/GetImageDescriptions"
        val jsonObjectRequestData = JSONObject()
        jsonObjectRequestData.put("imageId", imageId)
        var svgImageDescription: SvgImageDescription? = null
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObjectRequestData,
            { response ->
                svgImageDescription = Gson().fromJson(response.toString(),SvgImageDescription::class.java)
                view?.addImageDescriptionToList(svgImageDescription)
            },
            { error ->
                error.stackTrace
            }
        )
        queue.add(jsonObjectRequest)
    }

    fun getImageTestsFromServer(queue: RequestQueue, imageId: Int){
        val url = "http://157.158.57.124:50820/api/device/Tests/GetImageTests"
        val jsonObjectRequestData = JSONObject()
        jsonObjectRequestData.put("imageId", imageId)
        var tests: Tests? = null
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObjectRequestData,
            { response ->
                tests = Gson().fromJson(response.toString(),Tests::class.java)
                view?.addTestToList(imageId, tests)
            },
            { error ->
                error.stackTrace
            }
        )
        queue.add(jsonObjectRequest)
    }

}