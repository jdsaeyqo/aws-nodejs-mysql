package com.example.retrofit_php.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class DataModel {

    data class RegisterData(
        @SerializedName("name")
        val name : String,
        @SerializedName("email")
        val email : String,
        @SerializedName("password")
        val password : String

    ) {

    }

    data class LoginData(
        @SerializedName("email")
        val email : String,
        @SerializedName("password")
        val password : String
    ) {
    }

    @Parcelize
    data class UserData(
        var email: String?,
        var nickname : String? = null,
        var age : String? = null,
        var job : String? = null,
        var interest1: String? = null,
        var interest2: String? = null,
        var interest3: String?= null

    ) : Parcelable {


    }

    @Parcelize
    data class OtherData(
        var email: String,
        var nickname: String?
    ) : Parcelable {

    }


    data class FavoriteData(

        var imageUri : String? = null,
        var iLikeCount : Int = 0,
        var likemeCount : Int = 0,
        var iLike : MutableMap<String,Boolean> = HashMap(),
        var likeMe : MutableMap<String,Boolean> = HashMap()
    ){

    }

    data class ChatRoomData(
        var chatRoomName : String? = null,
        var otherEmail : String? = null,
        var userNickname : String? = null,
        var chatMessage : String? = "새로운 채팅방이 개설되었습니다"
    ){

    }

    data class ChatData(
        var nickname : String,
        var message : String,
        var date_time : String
    ){

    }



}