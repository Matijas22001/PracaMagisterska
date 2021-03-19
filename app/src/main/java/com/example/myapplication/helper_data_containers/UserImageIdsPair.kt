package com.example.myapplication.helper_data_containers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserImageIdsPair: Serializable {
    @SerializedName("userId")
    var userId: Int? = null

    @SerializedName("svgIdList")
    var svgIdListFromServer: ArrayList<Int>? = null

    constructor(userId: Int?, svgIdListFromServer: ArrayList<Int>?) {
        this.userId = userId
        this.svgIdListFromServer = svgIdListFromServer
    }
}