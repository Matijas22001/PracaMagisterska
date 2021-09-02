package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable

class OfflineClickList: Serializable {
    @SerializedName("offlineClickList")
    var offlineClickList: ArrayList<JSONObject>? = null
}