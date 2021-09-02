package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import pl.polsl.MathHelper.helper_data_containers.ChosenAnswersForTest
import java.io.Serializable

class OfflineTestList: Serializable {
    @SerializedName("offlineTestList")
    var offlineTestList: ArrayList<ChosenAnswersForTest>? = null
}