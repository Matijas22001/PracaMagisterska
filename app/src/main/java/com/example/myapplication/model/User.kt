package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class User: Serializable {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("userName")
    var userName: String? = null

    @SerializedName("disabled")
    var disabled: Boolean? = null

    @SerializedName("roles")
    var roles: ArrayList<String>? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("surname")
    var surname: String? = null

    @SerializedName("studentId")
    var studentId: Int? = null

    @SerializedName("teacherId")
    var teacherId: Int? = null
}