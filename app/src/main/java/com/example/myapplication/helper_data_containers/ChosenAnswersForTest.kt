package com.example.myapplication.helper_data_containers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChosenAnswersForTest: Serializable {
    @SerializedName("testId")
    var testId: Int? = null

    @SerializedName("ListOfQuestions")
    var answerId: ArrayList<ChosenAnswersForQuestion>? = null
}