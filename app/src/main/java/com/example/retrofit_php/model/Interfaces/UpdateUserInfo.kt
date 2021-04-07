package com.example.retrofit_php.model.Interfaces


import com.example.retrofit_php.model.data.UpdataUserdataResponse
import com.example.retrofit_php.model.data.UserData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UpdateUserInfo {

    @POST("/user/updateuserinfo")
    fun updateUserInfo(
        @Body userinfo : UserData

    ): Call<UpdataUserdataResponse>
}