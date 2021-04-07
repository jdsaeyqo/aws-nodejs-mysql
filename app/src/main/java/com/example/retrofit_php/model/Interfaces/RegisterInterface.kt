package com.example.retrofit_php.model.Interfaces

import com.example.retrofit_php.model.data.RegiResponse
import com.example.retrofit_php.model.data.RegisterData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RegisterInterface {
   @POST("/user/join")
   fun getUserRegist(
        @Body data : RegisterData
        ):Call<RegiResponse>

}