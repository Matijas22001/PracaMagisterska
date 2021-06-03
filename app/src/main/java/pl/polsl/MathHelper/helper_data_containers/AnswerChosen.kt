package pl.polsl.MathHelper.helper_data_containers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AnswerChosen : Serializable{
    @SerializedName("id")
    var answerId: Int? = null

    @SerializedName("isSelected")
    var isChosen: Boolean? = null

    constructor(answerId: Int?, isChosen: Boolean?) {
        this.answerId = answerId
        this.isChosen = isChosen
    }
}