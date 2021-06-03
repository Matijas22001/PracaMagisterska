package pl.polsl.MathHelper.ui.mainActivity

import androidx.appcompat.app.AlertDialog
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import pl.polsl.MathHelper.model.*
import com.google.gson.Gson
import org.json.JSONObject


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
}