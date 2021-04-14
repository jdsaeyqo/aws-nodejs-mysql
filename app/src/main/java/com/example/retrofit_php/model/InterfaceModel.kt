package com.example.retrofit_php.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface InterfaceModel {

    interface RegisterInterface {
        @POST("/user/join")
        fun getUserRegist(
            @Body data: DataModel.RegisterData
        ): Call<ResponseModel.RegisterResponse>

    }

    interface CheckNickInterface {
        @POST("/user/checknick")
        fun checkUserNick(
            @Body nickname: DataModel.CheckNickData
        ): Call<ResponseModel.RegisterResponse>
    }

    interface LoginInterface {

        @POST("/user/login")
        fun getUserLogin(
            @Body data: DataModel.LoginData
        ): Call<ResponseModel.LoginResponse>

    }

    interface GetUserInfoInterface {

        @GET("/user/getuserdata")
        fun getUserData(
            @Query("email") email: String
        ): Call<ResponseModel.GetUserDataResponse>


        @GET("/user/getmatchuser")
        fun getMatchingUser(
            @Query("interest1") interest1: String,
            @Query("interest2") interest2: String,
            @Query("interest3") interest3: String


        ): Call<ResponseModel.GetOtherDataResponse>
    }

    interface UpdateUserInfo {

        @POST("/user/updateuserinfo")
        fun updateUserInfo(
            @Body userinfo: DataModel.UserData

        ): Call<ResponseModel.UpdataUserdataResponse>
    }
}