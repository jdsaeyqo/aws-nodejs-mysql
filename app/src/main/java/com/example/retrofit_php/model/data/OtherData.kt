package com.example.retrofit_php.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OtherData(
    var email: String,
    var nickname: String?
    ) : Parcelable{

    }
