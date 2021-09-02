package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable

class OfflineTestList: Serializable {
    @SerializedName("offlineTestList")
    var offlineTestList: ArrayList<JSONObject>? = null
}