package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Student: Serializable {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("surname")
    var surname: String? = null

    constructor(id: Int?, name: String?, surname: String?) {
        this.id = id
        this.name = name
        this.surname = surname
    }
}