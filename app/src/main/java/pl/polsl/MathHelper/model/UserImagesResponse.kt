package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserImagesResponse: Serializable {
    @SerializedName("svgIdList")
    var svgIdList: ArrayList<Int>? = null

    constructor(svgIdList: ArrayList<Int>?) {
        this.svgIdList = svgIdList
    }
}