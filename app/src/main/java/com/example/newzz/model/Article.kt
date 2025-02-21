package com.example.newzz.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName ="articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val author: String?,
    val content: String?,
    var description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    var urlToImage: String?
) : Serializable