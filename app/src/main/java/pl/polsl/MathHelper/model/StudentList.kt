package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StudentList: Serializable {
    @SerializedName("studentsList")
    var studentsList: List<Student>? = null
}