package com.example.retrofit_php.model.Interfaces

import com.example.retrofit_php.model.data.LoginData
import com.example.retrofit_php.model.data.LoginResponse
import com.example.retrofit_php.model.data.RegiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginInterface {

    @POST("/user/login")
    fun getUserLogin(
        @Body data : LoginData
    ):Call<LoginResponse>

}