package pl.polsl.MathHelper.ui.userListActivity

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import pl.polsl.MathHelper.model.StudentListResponse
import com.google.gson.Gson

class UserListActivityPresenter(private val view: UserListActivityView?, private val navigator: UserListActivityNavigator?){
    fun getUserListFromServer(queue: RequestQueue, token: String){
        val url = "http://157.158.57.124:50820/api/RemoteLearning/GetStudentsOnline"
        var studentListResponse: StudentListResponse? = null
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            Response.Listener { response ->
                studentListResponse = Gson().fromJson(response.toString(),StudentListResponse::class.java)
                view?.updateRecyclerView(studentListResponse)
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