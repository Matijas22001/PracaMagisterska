package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Test: Serializable {
    @SerializedName("id")
    var testId: Int? = null

    @SerializedName("svgId")
    var svgId: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("maxPoints")
    var maxPoints: Int? = null

    @SerializedName("questionsList")
    var questionList: ArrayList<Question>? = null
}