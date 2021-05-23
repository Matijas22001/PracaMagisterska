package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ClickSendObject(@SerializedName("click") var click: List<Click>?) :
    Serializable