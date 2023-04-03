package com.example.newsapppaging.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<Article>)

    @Query(
        "SELECT * FROM articleTable"
    )
    fun getAllArticles(): PagingSource<Int, Article>

    @Query("DELETE FROM articleTable")
    suspend fun clearArticles()
}