package com.example.retrofit_php.model

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Repository {

    private const val BASE_URL = "http://ec2-18-219-100-91.us-east-2.compute.amazonaws.com:9000/"
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