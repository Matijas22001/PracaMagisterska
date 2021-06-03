package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OnClickDescription: Serializable {
    @SerializedName("testId")
    var testId: Int? = null

    @SerializedName("questionId")
    var questionId: Int? = null

    @SerializedName("oneClick")
    var oneClick: String? = null

    @SerializedName("doubleClick")
    var doubleClick: String? = null

    @SerializedName("tripleClick")
    var tripleClick: String? = null
}