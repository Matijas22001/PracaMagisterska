package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SvgModel: Serializable {
    @SerializedName("pathId")
    var pathId: String? = null

    @SerializedName("object")
    var onClickDescriptions: ArrayList<OnClickDescription>? = null
}