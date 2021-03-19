package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SvgImageDescription: Serializable {
    @SerializedName("svgId")
    var svgId: Int? = null

    @SerializedName("svgModel")
    var svgModel: ArrayList<SvgModel>? = null

    constructor(svgId: Int?, svgModel: ArrayList<SvgModel>?) {
        this.svgId = svgId
        this.svgModel = svgModel
    }
}