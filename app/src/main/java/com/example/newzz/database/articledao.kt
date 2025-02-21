package com.example.newzz.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newzz.model.Article

@Dao
interface articledao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article):Long
    @Query("SELECT * FROM articles")
    fun getarticles(): LiveData<List<Article>>
    @Delete
    suspend fun deletearticle(article: Article)

}