package com.example.retrofit_php.model.data

import com.google.gson.annotations.SerializedName

data class RegisterData(
    @SerializedName("name")
    val name : String,
    @SerializedName("email")
    val email : String,
    @SerializedName("password")
    val password : String

) {

}


