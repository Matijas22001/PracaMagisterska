package com.example.myapplication.ui.questionActivity

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.example.myapplication.helper_data_containers.ChosenAnswersForQuestion
import com.example.myapplication.helper_data_containers.ChosenAnswersForTest
import com.example.myapplication.model.Student
import com.example.myapplication.model.StudentList
import com.example.myapplication.ui.testActivity.TestActivityNavigator
import com.example.myapplication.ui.testActivity.TestActivityView
import com.example.myapplication.utils.AppPreferences
import com.google.gson.Gson
import org.json.JSONObject

class QuestionActivityPresenter (private val view: QuestionActivityView?, private val navigator: QuestionActivityNavigator?){

    fun sendTestToServer(queue: RequestQueue){
        val url = "http://157.158.57.124:50820/api/device/TestResults/SaveResults"
        //var studentList: StudentList? = null
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, createPOSTObject(),
                { response ->
                    //studentList = Gson().fromJson(response.toString(), StudentList::class.java)
                    //studentListToShow.addAll(studentList?.studentsList!!)
                    //view?.updateRecyclerView()
                },
                { error ->
                    //error.stackTrace
                }
        )
        queue.add(jsonObjectRequest)
    }

    private fun createPOSTObject(): JSONObject{
        var jsonObject = JSONObject()
        var chosenAnswersForTest: ChosenAnswersForTest = Gson().fromJson(AppPreferences.answerList, ChosenAnswersForTest::class.java)
        //ToDo
        return jsonObject
    }
}
