package com.example.retrofit_php.model

import com.google.gson.annotations.SerializedName

class ResponseModel {

    data class RegisterResponse(
        @SerializedName("code")
        val code: Int,

        @SerializedName("message")
        val message: String

    ) {
    }

    data class LoginResponse(
        @SerializedName("code")
        val code: Int,

        @SerializedName("message")
        val message: String,

        @SerializedName("id")
        val id : Int

    ) {
    }

    data class GetUserDataResponse(

        val email : String,
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
        val interest3: String

    ) {
    }
    data class GetOtherDataResponse(
        @SerializedName("result")
        val result : List<DataModel.OtherData>
    ) {
    }

    data class UpdataUserdataResponse(

        @SerializedName("message")
        val message: String
    ) {
    }
}