package com.example.myapplication.helper_data_containers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChosenAnswersForTest: Serializable {
    @SerializedName("testId")
    var testId: Int? = null

    @SerializedName("studentId")
    var studentId: Int? = null

    @SerializedName("startDate")
    var startDate: String? = null

    @SerializedName("endDate")
    var endDate: String? = null

    @SerializedName("questionsList")
    var listOfQuestions: ArrayList<ChosenAnswersForQuestion>? = null
}