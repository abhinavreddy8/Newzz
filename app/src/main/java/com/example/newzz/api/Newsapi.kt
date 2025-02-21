package com.example.newzz.api

import com.example.newzz.model.Response
import com.example.newzz.utils.const.Companion.Apikey
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface Newsapi {
    @GET("v2/top-headlines")
    suspend fun getheadlines(
        @Query("country")
        country: String = "in",
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = Apikey
    ): retrofit2.Response<Response>

    @GET("v2/everything")
    suspend fun searchnews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = Apikey
    ):retrofit2.Response<Response>


}