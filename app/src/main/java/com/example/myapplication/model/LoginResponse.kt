package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LoginResponse: Serializable {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("userName")
    var userName: String? = null

    @SerializedName("disabled")
    var disabled: Boolean? = null

    @SerializedName("roles")
    var roles: ArrayList<String>? = null
}
