package com.example.retrofit_php.model.data

import com.google.gson.annotations.SerializedName

data class GetUserData(

    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("age")
    val age :String,
    @SerializedName("job")
    val job: String,
    @SerializedName("interest1")
    val interest1: String,
    @SerializedName("interest2")
    val interest2: String,
    @SerializedName("interest3")
    val interest3: String,
) {
}