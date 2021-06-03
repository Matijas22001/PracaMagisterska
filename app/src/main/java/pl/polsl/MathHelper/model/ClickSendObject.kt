package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ClickSendObject(@SerializedName("click") var click: List<Click>?) :
    Serializable