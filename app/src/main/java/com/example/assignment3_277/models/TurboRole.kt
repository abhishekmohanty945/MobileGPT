package com.example.assignment3_277.models

import com.google.gson.annotations.SerializedName

enum class TurboRole(val value: String) {
    @SerializedName("system")
    system("system"),
    @SerializedName("assistant")
    assistant("assistant"),
    @SerializedName("user")
    user("user")
}