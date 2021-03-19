package com.example.myapplication.helper_data_containers

import com.example.myapplication.model.Tests
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