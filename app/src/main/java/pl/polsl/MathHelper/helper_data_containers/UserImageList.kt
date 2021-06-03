package pl.polsl.MathHelper.helper_data_containers

import pl.polsl.MathHelper.model.SvgImage
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