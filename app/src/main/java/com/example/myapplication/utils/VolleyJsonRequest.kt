package com.example.myapplication.utils

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.io.UnsupportedEncodingException

open class VolleyJsonRequest : JsonObjectRequest {
    constructor(
        method: Int,
        url: String?,
        jsonRequest: JSONObject?,
        listener: Response.Listener<JSONObject?>?,
        errorListener: Response.ErrorListener?
    ) : super(method, url, jsonRequest, listener, errorListener) {
    }

    constructor(
        url: String?,
        jsonRequest: JSONObject?,
        listener: Response.Listener<JSONObject?>?,
        errorListener: Response.ErrorListener?
    ) : super(url, jsonRequest, listener, errorListener) {
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        var response = response
        try {
            if (response.data.isEmpty()) {
                val responseData = "{}".toByteArray(charset("UTF8"))
                response = NetworkResponse(
                    response.statusCode,
                    responseData,
                    response.headers,
                    response.notModified
                )
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return super.parseNetworkResponse(response)
    }
}