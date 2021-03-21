package com.example.myapplication.helper_data_containers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AnswerChosen : Serializable{
    @SerializedName("answerId")
    var answerId: Int? = null

    @SerializedName("isChosen")
    var isChosen: Boolean? = null

    constructor(answerId: Int?, isChosen: Boolean?) {
        this.answerId = answerId
        this.isChosen = isChosen
    }
}