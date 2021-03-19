package com.example.myapplication.api

import com.example.myapplication.view_binding.BaseView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

abstract class ApiCallback<T>(private val baseView: BaseView, private val userService: UserService) : Callback<T> {
    abstract fun onSuccess(t: T?)
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onSuccess(response.body())
        } else {
            baseView.showMessage(getErrorMessage(response.errorBody()))
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        baseView.showMessage(t.message)
    }

    companion object {
        fun getErrorMessage(responseBody: ResponseBody?): String? {
            try {
                return responseBody?.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return "pusty błąd"
        }
    }
}