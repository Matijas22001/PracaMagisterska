package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Answer: Serializable {
    @SerializedName("id")
    var answerId: Int? = null

    @SerializedName("description")
    var answerDescription: String? = null

    @SerializedName("isCorrect")
    var answerIsCorrect: Boolean? = null

    @SerializedName("points")
    var answerPoints: Double? = null
}