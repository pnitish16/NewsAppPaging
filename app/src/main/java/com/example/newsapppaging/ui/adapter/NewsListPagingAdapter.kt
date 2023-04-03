package com.example.newsapppaging.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapppaging.data.database.Article
import com.example.newsapppaging.data.network.model.ArticlesItem
import com.example.newsapppaging.databinding.ListItemNewsBinding

class NewsListPagingAdapter(
    private val onItemClick: OnItemClickListener
) :
    PagingDataAdapter<Article, NewsListPagingAdapter.UserListViewHolder>(
        COMPARATOR
    ) {

    inner class UserListViewHolder(private val binding: ListItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(articlesItem: Article) {
            binding.newsItem = articlesItem
        }
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        getItem(position)?.let { userItem ->
            holder.bind(userItem)
            holder.itemView.setOnClickListener {
                onItemClick.onItemClick(userItem)
            }
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): UserListViewHolder {
        val view = ListItemNewsBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return UserListViewHolder(view)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(
                oldItem: Article,
                newItem: Article
            ): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(
                oldItem: Article,
                newItem: Article
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(articlesItem: Article)
    }
}