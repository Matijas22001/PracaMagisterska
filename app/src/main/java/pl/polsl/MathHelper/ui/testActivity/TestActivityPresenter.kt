package pl.polsl.MathHelper.ui.testActivity

import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import pl.polsl.MathHelper.model.Click
import pl.polsl.MathHelper.model.ClickSendObject
import pl.polsl.MathHelper.utils.AppPreferences
import pl.polsl.MathHelper.utils.VolleyJsonRequest
import com.google.gson.Gson
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.json.JSONObject

class TestActivityPresenter(private val view: TestActivityView?, private val navigator: TestActivityNavigator?){
    fun sendImageClickDataToServer(queue: RequestQueue, x: Long?, y: Long?, elementId: String, fileId: Int, token: String, type: Int){
        val url = "http://157.158.57.124:50820/api/MotionLog/SaveClicks"
        val jsonObjectRequest: VolleyJsonRequest = object : VolleyJsonRequest(
            Method.POST, url, createPOSTObject(x, y, elementId, fileId, type),
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

    private fun createPOSTObject(x: Long?, y: Long?, elementId: String, fileId: Int, type: Int): JSONObject? {
        return try{
            val click = Click()
            click.studentId = AppPreferences.chosenUser
            click.fileId = fileId
            click.x = x
            click.y = y
            click.elementId = elementId
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
