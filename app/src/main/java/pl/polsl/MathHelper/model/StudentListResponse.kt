package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StudentListResponse : Serializable {
    @SerializedName("list")
    var list: ArrayList<Student>? = null

    @SerializedName("pageNumber")
    var pageNumber: Int? = null

    @SerializedName("pageSize")
    var pageSize: Int? = null

    @SerializedName("totalPages")
    var totalPages: Int? = null

    @SerializedName("totalCount")
    var totalCount: Int? = null
}