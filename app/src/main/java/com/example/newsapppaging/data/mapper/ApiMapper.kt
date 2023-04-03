package com.example.newsapppaging.data.mapper

import com.example.newsapppaging.data.database.Article
import com.example.newsapppaging.data.network.model.ArticlesItem

interface ApiMapper {

  fun mapApiToDatabase(articlesItem: ArticlesItem): Article
}