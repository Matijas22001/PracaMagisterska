package com.example.myapplication.ui.questionActivity

import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.myapplication.helper_data_containers.ChosenAnswersForTest
import com.example.myapplication.helper_data_containers.ChosenAnswersForTestList
import com.example.myapplication.model.Click
import com.example.myapplication.model.ClickSendObject
import com.example.myapplication.utils.AppPreferences
import com.example.myapplication.utils.VolleyJsonRequest
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.json.JSONObject


class QuestionActivityPresenter(private val view: QuestionActivityView?, private val navigator: QuestionActivityNavigator?) {

    fun sendTestToServer(queue: RequestQueue, token: String){
        val url = "http://157.158.57.124:50820/api/device/TestResults/SaveResults"
        val jsonObjectRequest: VolleyJsonRequest = object : VolleyJsonRequest(
            Method.POST, url, createPOSTObject(),
            Response.Listener { response ->
                AppPreferences.answerList = ""
                view?.sendTestAndCloseActivity()
            }, Response.ErrorListener { error ->
                AppPreferences.answerList = ""
                view?.sendTestAndCloseActivity()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $token"
                return params
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun createPOSTObject(): JSONObject? {
        return try{
            val chosenAnswersForTest: ChosenAnswersForTest = Gson().fromJson(AppPreferences.answerList, ChosenAnswersForTest::class.java)
            chosenAnswersForTest.testId = AppPreferences.chosenTestId
            chosenAnswersForTest.studentId = AppPreferences.chosenUser
            chosenAnswersForTest.startDate = Hawk.get("Test_start")
            chosenAnswersForTest.endDate = Hawk.get("Test_end")
            val tempList: ArrayList<ChosenAnswersForTest> = ArrayList()
            tempList.add(chosenAnswersForTest)
            JSONObject(Gson().toJson(ChosenAnswersForTestList(tempList)))
        }catch (e: Exception){
            null
        }
    }

    fun sendImageClickDataToServer(queue: RequestQueue, x: Long, y: Long, elementId: String, fileId: Int, token: String){
        val url = "http://157.158.57.124:50820/api/device/Clicks/SaveClicks"
        val jsonObjectRequest: VolleyJsonRequest = object : VolleyJsonRequest(
            Method.POST, url, createPOSTObject(x, y, elementId, fileId),
            Response.Listener { response ->
                Log.i("Click", "Saved")
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

    private fun createPOSTObject(x: Long, y: Long, elementId: String, fileId: Int): JSONObject? {
        return try{
            val click = Click()
            click.studentId = AppPreferences.chosenUser
            click.fileId = fileId
            click.x = x
            click.y = y
            click.elementId = elementId
            click.timeStamp = getTime()
            val tempList: ArrayList<Click> = ArrayList()
            tempList.add(click)
            JSONObject(Gson().toJson(ClickSendObject(tempList)))
        }catch (e: Exception){
            null
        }
    }

    fun getTime(): String {
        val dt = DateTime.now()
        val fmt: DateTimeFormatter = ISODateTimeFormat.dateTime()
        return fmt.print(dt)
    }


}
