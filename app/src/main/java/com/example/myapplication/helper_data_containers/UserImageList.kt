package com.example.myapplication.helper_data_containers

import com.example.myapplication.model.SvgImage
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserImageList: Serializable {
    @SerializedName("userId")
    var userId: Int? = null

    @SerializedName("svgImagesList")
    var svgImagesListFromServer: ArrayList<SvgImage>? = null

    constructor(userId: Int?, svgImagesListFromServer: ArrayList<SvgImage>?) {
        this.userId = userId
        this.svgImagesListFromServer = svgImagesListFromServer
    }
}