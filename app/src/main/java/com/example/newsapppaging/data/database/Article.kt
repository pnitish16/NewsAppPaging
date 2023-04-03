package com.example.newsapppaging.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "articleTable")
data class Article(
    @PrimaryKey @field:SerializedName("url") val url: String,
    @field:SerializedName("author") val author: String,
    @field:SerializedName("urlToImage") val urlToImage: String,
    @field:SerializedName("description") val description: String?,
    @field:SerializedName("source") val source: String,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("content") val content: String?
)