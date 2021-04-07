package com.example.retrofit_php.model.data

import com.google.gson.annotations.SerializedName

data class GetOtherDataResponse(
    @SerializedName("result")
    val result : List<OtherData>
) {
}