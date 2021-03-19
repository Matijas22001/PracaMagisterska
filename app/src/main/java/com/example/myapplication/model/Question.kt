package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Question: Serializable {
    @SerializedName("id")
    var questionId: Int? = null

    @SerializedName("description")
    var questionDescription: String? = null

    @SerializedName("answersList")
    var answerList: ArrayList<Answer>? = null
}