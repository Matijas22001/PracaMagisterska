package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Click: Serializable {
    @SerializedName("studentId")
    var studentId: Int? = null

    @SerializedName("fileId")
    var fileId: Int? = null

    @SerializedName("testId")
    var testId: Int? = null

    @SerializedName("questionId")
    var questionId: Int? = null

    @SerializedName("x")
    var x: Long? = null

    @SerializedName("y")
    var y: Long? = null

    @SerializedName("elementId")
    var elementId: String? = null

    @SerializedName("timeStamp")
    var timeStamp: String? = null
}