package com.example.newzz.api

import com.example.newzz.utils.const.Companion.Baseurl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class retrofitinstance {
    companion object {
        private val retrofit by lazy {
            val logg = HttpLoggingInterceptor()
            logg.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logg).build()

            Retrofit.Builder().baseUrl(Baseurl).addConverterFactory(GsonConverterFactory.create())
                .client(client).build()

        }
        val api by lazy {
            retrofit.create(Newsapi::class.java)
        }
    }
}