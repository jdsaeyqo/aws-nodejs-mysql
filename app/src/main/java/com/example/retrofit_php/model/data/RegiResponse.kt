package com.example.retrofit_php.model.data

import com.google.gson.annotations.SerializedName

data class RegiResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("message")
    val message: String

) {
}