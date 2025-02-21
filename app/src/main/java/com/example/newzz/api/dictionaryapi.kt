package com.example.newzz.api

import com.example.newzz.model.DictionaryItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface dictionaryapi {
    @GET("entries/en/{word}")
    suspend fun getMeaning(@Path("word") word: String): Response<List<DictionaryItem>>
}