package pl.polsl.MathHelper.helper_data_containers

import pl.polsl.MathHelper.model.Tests
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ImageIdTestsForImage: Serializable {
    @SerializedName("imageId")
    var imageId: Int? = null

    @SerializedName("tests")
    var tests: Tests? = null

    constructor(imageId: Int?, tests: Tests?) {
        this.imageId = imageId
        this.tests = tests
    }
}