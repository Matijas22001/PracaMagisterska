package pl.polsl.MathHelper.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SvgImage: Serializable {
    @SerializedName("svgId")
    var svgId: Int? = null

    @SerializedName("svgTitle")
    var svgTitle: String? = null

    @SerializedName("svgDirectory")
    var svgDirectory: String? = null

    @SerializedName("svgXML")
    var svgXML: String? = null

    constructor(svgId: Int?, svgTitle: String?, svgDirectory: String?, svgXML: String?) {
        this.svgId = svgId
        this.svgTitle = svgTitle
        this.svgDirectory = svgDirectory
        this.svgXML = svgXML
    }
}