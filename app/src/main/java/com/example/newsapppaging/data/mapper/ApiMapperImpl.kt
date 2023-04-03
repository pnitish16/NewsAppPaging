package com.example.newsapppaging.data.mapper

import com.example.newsapppaging.data.database.Article
import com.example.newsapppaging.data.network.model.ArticlesItem

class ApiMapperImpl : ApiMapper {
    override fun mapApiToDatabase(articlesItem: ArticlesItem): Article {
        return Article(
            articlesItem.url ?: "",
            articlesItem.author ?: "",
            articlesItem.urlToImage ?: "",
            articlesItem.description ?: "",
            articlesItem.source?.name ?: "",
            articlesItem.title ?: "",
            articlesItem.content ?: "",
        )
    }
}