package com.example.myapplication.ui.questionActivity

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.example.myapplication.helper_data_containers.ChosenAnswersForTest
import com.example.myapplication.helper_data_containers.ChosenAnswersForTestList
import com.example.myapplication.utils.AppPreferences
import com.google.gson.Gson
import org.json.JSONObject
import com.google.gson.JsonParser
import java.lang.Exception


class QuestionActivityPresenter(private val view: QuestionActivityView?, private val navigator: QuestionActivityNavigator?) {

    fun sendTestToServer(queue: RequestQueue) {
        val url = "http://157.158.57.124:50820/api/device/TestResults/SaveResults"
        //var studentList: StudentList? = null
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, createPOSTObject(),
                {
                    AppPreferences.answerList = ""
                    view?.sendTestAndCloseActivity()
                },
                {
                    AppPreferences.answerList = ""
                    view?.sendTestAndCloseActivity()
                }

        )
        queue.add(jsonObjectRequest)
    }

    private fun createPOSTObject(): JSONObject? {
        return try{
            val chosenAnswersForTest: ChosenAnswersForTest = Gson().fromJson(AppPreferences.answerList, ChosenAnswersForTest::class.java)
            chosenAnswersForTest.testId = AppPreferences.chosenTestId
            chosenAnswersForTest.studentId = AppPreferences.chosenUser
            chosenAnswersForTest.startDate = ""
            chosenAnswersForTest.endDate = ""
            val tempList: ArrayList<ChosenAnswersForTest> = ArrayList()
            tempList.add(chosenAnswersForTest)
            JSONObject(Gson().toJson(ChosenAnswersForTestList(tempList)))
        }catch (e: Exception){
            null
        }
    }
}
