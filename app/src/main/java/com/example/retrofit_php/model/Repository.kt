package com.example.retrofit_php.model

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Repository {
    //EC2 퍼블릭 주소
    private const val BASE_URL = ""
    var retrofit: Retrofit? = null

    fun getApiClient(): Retrofit? {
        val gson =
            GsonBuilder()
                .setLenient()
                .create()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit

    }
}
