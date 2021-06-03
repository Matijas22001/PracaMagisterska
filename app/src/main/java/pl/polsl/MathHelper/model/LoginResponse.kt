package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LoginResponse: Serializable {
    @SerializedName("user")
    var user: User? = null

    @SerializedName("token")
    var token: String? = null
}
