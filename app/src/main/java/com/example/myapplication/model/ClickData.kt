package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ClickData: Serializable {
    @SerializedName("click")
    var clickList: ArrayList<Click>? = null

    @SerializedName("move")
    var moveList: ArrayList<Move>? = null
}