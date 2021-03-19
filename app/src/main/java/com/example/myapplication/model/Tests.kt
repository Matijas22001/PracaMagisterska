package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Tests: Serializable {
    @SerializedName("tests")
    var testList: ArrayList<Test>? = null
}