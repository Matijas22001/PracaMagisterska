package com.example.myapplication.helper_data_containers

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChosenAnswersForTestList(@SerializedName("testResults") var testResults: List<ChosenAnswersForTest>?) : Serializable