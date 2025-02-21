package com.example.newzz.repository

import android.util.Log
import com.example.newzz.api.retrofitinstance
import com.example.newzz.api.retrofitweatherinstance
import com.example.newzz.database.articledatabase
import com.example.newzz.model.Article


class newsrepository(val db: articledatabase) {
    suspend fun getheadlines(country: String, page: Int)=
        retrofitinstance.api.getheadlines(country, page)
    suspend fun searchnews(searchQuery: String, page: Int)=
        retrofitinstance.api.searchnews(searchQuery, page)
    suspend fun insert(article: Article)=db.dao().insert(article)

    suspend fun deletearticle(article: Article)=db.dao().deletearticle(article)
    fun getsavedarticles()=db.dao().getarticles()
    fun getweather(city: String, appid: String, units: String)=
        retrofitweatherinstance.weatherapi.getweather(city, appid, units)

}