package pl.polsl.MathHelper.ui.questionActivity

import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import pl.polsl.MathHelper.helper_data_containers.ChosenAnswersForTest
import pl.polsl.MathHelper.helper_data_containers.ChosenAnswersForTestList
import pl.polsl.MathHelper.model.Click
import pl.polsl.MathHelper.model.ClickSendObject
import pl.polsl.MathHelper.utils.AppPreferences
import pl.polsl.MathHelper.utils.VolleyJsonRequest
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

    fun sendImageClickDataToServer(queue: RequestQueue, x: Long?, y: Long?, elementId: String, fileId: Int, testId: Int, token: String, type: Int){
        val url = "http://157.158.57.124:50820/api/MotionLog/SaveClicks"
        val jsonObjectRequest: VolleyJsonRequest = object : VolleyJsonRequest(
            Method.POST, url, createPOSTObject(x, y, elementId, fileId, testId, type),
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

    private fun createPOSTObject(x: Long?, y: Long?, elementId: String, fileId: Int, testId: Int, type: Int): JSONObject? {
        return try{
            val click = Click()
            click.studentId = AppPreferences.chosenUser
            click.fileId = fileId
            click.x = x
            click.y = y
            click.elementId = elementId
            click.testId = testId
            click.timeStamp = getTime()
            click.type = type
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
