package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Move: Serializable {
    @SerializedName("studentId")
    var studentId: Int? = null

    @SerializedName("fileId")
    var fileId: Int? = null

    @SerializedName("testId")
    var testId: Int? = null

    @SerializedName("questionId")
    var questionId: Int? = null

    @SerializedName("x")
    var x: Int? = null

    @SerializedName("y")
    var y: Int? = null

    @SerializedName("elementId")
    var elementId: String? = null

    @SerializedName("timeStamp")
    var timeStamp: String? = null
}