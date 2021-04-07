package com.example.retrofit_php.model.Interfaces

import com.example.retrofit_php.model.data.GetOtherDataResponse
import com.example.retrofit_php.model.data.GetUserDataResponse
import com.example.retrofit_php.model.data.LoginData
import com.example.retrofit_php.model.data.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface GetUserInfoInterface {

    @GET("/user/getuserdata")
    fun getUserData(
        @Query("email") email : String
    ):Call<GetUserDataResponse>


    @GET("/user/getmatchuser")
    fun getMatchingUser(
        @Query("interest1") interest1 : String,
        @Query("interest2") interest2 : String,
        @Query("interest3") interest3 : String


    ): Call<GetOtherDataResponse>
}