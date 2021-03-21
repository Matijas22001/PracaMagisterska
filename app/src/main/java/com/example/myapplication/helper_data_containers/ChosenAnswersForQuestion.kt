package com.example.myapplication.helper_data_containers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChosenAnswersForQuestion: Serializable {
    @SerializedName("questionId")
    var questionId: Int? = null

    @SerializedName("ListOfAnswers")
    var listOfAnswers: ArrayList<AnswerChosen>? = null

    constructor(questionId: Int?, listOfAnswers: ArrayList<AnswerChosen>?) {
        this.questionId = questionId
        this.listOfAnswers = listOfAnswers
    }
}