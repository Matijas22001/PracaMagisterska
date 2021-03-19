package com.example.myapplication.helper_data_containers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChosenAnswersForQuestion: Serializable {
    @SerializedName("questionId")
    var questionId: Int? = null

    @SerializedName("ListOfAnswers")
    var answerId: ArrayList<AnswerChosen>? = null
}