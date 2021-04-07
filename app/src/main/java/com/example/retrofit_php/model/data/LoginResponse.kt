package com.example.retrofit_php.model.data

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("email")
    val email: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("id")
    val id : Int

) {
}