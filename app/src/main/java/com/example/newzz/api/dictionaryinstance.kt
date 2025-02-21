package com.example.newzz.api

import com.example.newzz.utils.const.Companion.Baseurl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class dictionaryinstance {
    companion object {
        private val retrofit by lazy {
            val logg = HttpLoggingInterceptor()
            logg.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logg).build()

            Retrofit.Builder().baseUrl("https://api.dictionaryapi.dev/api/v2/").addConverterFactory(GsonConverterFactory.create())
                .client(client).build()

        }
        val api =
            retrofit.create(dictionaryapi::class.java)

    }
}