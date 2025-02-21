package com.example.newzz.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.newzz.weatherapp

interface weatherapi {
    @GET("weather")
    fun getweather(
        @Query("q") city: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<weatherapp>
}
