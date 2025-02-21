package com.example.newzz.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class retrofitweatherinstance {
    companion object {
        private val weatherretrofit by lazy {
            val logg = HttpLoggingInterceptor()
            logg.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logg).build()
            Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/").addConverterFactory(GsonConverterFactory.create())
                .client(client).build()

        }
        val weatherapi by lazy {
            weatherretrofit.create(weatherapi::class.java)
        }


    }
}