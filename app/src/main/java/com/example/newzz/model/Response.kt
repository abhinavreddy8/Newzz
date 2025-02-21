package com.example.newzz.model

data class Response(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)