package com.example.newsapppaging.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("normalPhoto")
    fun loadNormalPhoto(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            Picasso.get().load(url).into(view)
        }
    }
}