package pl.polsl.MathHelper.ui.mainActivity

import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import pl.polsl.MathHelper.model.*
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.json.JSONObject
import pl.polsl.MathHelper.helper_data_containers.ChosenAnswersForTest
import pl.polsl.MathHelper.helper_data_containers.ChosenAnswersForTestList
import pl.polsl.MathHelper.utils.AppPreferences
import pl.polsl.MathHelper.utils.VolleyJsonRequest


class MainActivityPresenter(private val view: MainActivityView?, private val navigator: MainActivityNavigator?){
    fun loginUser(queue: RequestQueue, login: String, password: String, dialog: AlertDialog){
        val url = "http://157.158.57.124:50820/api/device/Auth/Login"
        var loginResponse: LoginResponse? = null
        val jsonObjectRequestData = JSONObject()
        jsonObjectRequestData.put("userName", login)
        jsonObjectRequestData.put("password", password)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObjectRequestData,
            { response ->
                loginResponse = Gson().fromJson(response.toString(), LoginResponse::class.java)
                view?.userLoggedInSuccessfulLogic(loginResponse!!, dialog)
            },
            { error ->
                view?.userLoggedInFailedLogic()
                error.stackTrace
            }
        )
        queue.add(jsonObjectRequest)
    }


    fun getUserImageIdsFromServer(queue: RequestQueue, studentId: Int, token: String){
        val url = "http://157.158.57.124:50820/api/device/Images/GetStudentImageIds"
        val jsonObjectRequestData = JSONObject()
        jsonObjectRequestData.put("studentId", studentId)
        var userImagesResponse: UserImagesResponse? = null
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObjectRequestData,
            { response ->
                userImagesResponse = Gson().fromJson(response.toString(), UserImagesResponse::class.java)
                view?.addElementToList(studentId, userImagesResponse?.svgIdList)
            },
            { error ->
                error.stackTrace
            }) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json"
                params["Connection"] = "keep-alive"
                params["Accept-Encoding"] = "giz, deflate, br"
                params["Authorization"] = "Bearer $token"
                return params
            }
        }
        queue.add(jsonObjectRequest)
    }


    fun getUserImageFromServer(queue: RequestQueue, imageId: Int, token: String){
        val url = "http://157.158.57.124:50820/api/device/Images/GetImage"
        val jsonObjectRequestData = JSONObject()
        jsonObjectRequestData.put("imageId", imageId)
        var svgImage: SvgImage? = null
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObjectRequestData,
            Response.Listener { response ->
                svgImage = Gson().fromJson(response.toString(), SvgImage::class.java)
                view?.addImageToList(svgImage)
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

    fun getImageDescriptionFromServer(queue: RequestQueue, imageId: Int, token: String){
        val url = "http://157.158.57.124:50820/api/device/Descriptions/GetImageDescriptions"
        val jsonObjectRequestData = JSONObject()
        jsonObjectRequestData.put("imageId", imageId)
        var svgImageDescription: SvgImageDescription? = null
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObjectRequestData,
            Response.Listener { response ->
                svgImageDescription = Gson().fromJson(response.toString(), SvgImageDescription::class.java)
                view?.addImageDescriptionToList(svgImageDescription)
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

    fun getImageTestsFromServer(queue: RequestQueue, imageId: Int, token: String){
        val url = "http://157.158.57.124:50820/api/device/Tests/GetImageTests"
        val jsonObjectRequestData = JSONObject()
        jsonObjectRequestData.put("imageId", imageId)
        var tests: Tests? = null
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObjectRequestData,
            Response.Listener { response ->
                tests = Gson().fromJson(response.toString(), Tests::class.java)
                view?.addTestToList(imageId, tests)
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

    fun sendTestToServer(queue: RequestQueue, chosenAnswersForTest: ArrayList<ChosenAnswersForTest>, token: String){
        val url = "http://157.158.57.124:50820/api/device/TestResults/SaveResults"
        val jsonObjectRequest: VolleyJsonRequest = object : VolleyJsonRequest(
            Method.POST, url, createPOSTTestObject(chosenAnswersForTest),
            Response.Listener { response ->
                AppPreferences.answerList = ""
                AppPreferences.offlineTests = ""
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

    private fun createPOSTTestObject(chosenAnswersForTest: ArrayList<ChosenAnswersForTest>): JSONObject? {
        return try{
            val tempList: ArrayList<ChosenAnswersForTest> = ArrayList()
            for(chosenAnswer in chosenAnswersForTest){
                tempList.add(chosenAnswer)
            }
            JSONObject(Gson().toJson(ChosenAnswersForTestList(tempList)))
        }catch (e: Exception){
            null
        }
    }

    fun sendImageClickDataToServer(queue: RequestQueue, clicks: ArrayList<Click>, token: String){
        val url = "http://157.158.57.124:50820/api/MotionLog/SaveClicks"
        val jsonObjectRequest: VolleyJsonRequest = object : VolleyJsonRequest(
            Method.POST, url, createPOSTClickObject(clicks),
            Response.Listener { response ->
                Log.i("Click", "Saved")
                AppPreferences.offlineClicks = ""
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

    private fun createPOSTClickObject(clicks: ArrayList<Click>): JSONObject? {
        return try{
            val tempList: ArrayList<Click> = ArrayList()
            for(click in clicks){
                tempList.add(click)
            }
            JSONObject(Gson().toJson(ClickSendObject(tempList)))
        }catch (e: Exception){
            null
        }
    }
}