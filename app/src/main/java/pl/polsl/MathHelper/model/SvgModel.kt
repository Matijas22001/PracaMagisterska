package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SvgModel: Serializable {
    @SerializedName("pathId")
    var pathId: String? = null

    @SerializedName("oneClick")
    var defaultOneClick: String? = null

    @SerializedName("doubleClick")
    var defaultDoubleClick: String? = null

    @SerializedName("tripleClick")
    var defaultTripleClick: String? = null

    @SerializedName("object")
    var onClickDescriptions: ArrayList<OnClickDescription>? = null
}