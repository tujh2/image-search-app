package com.wnp.imagesearch

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey

class ImagesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = "Image View Holde"
    private val imageView: ImageView = itemView.findViewById(R.id.seatch_item_image)
    private val siteUrl: TextView = itemView.findViewById(R.id.search_item_link)
    private val descr: TextView = itemView.findViewById(R.id.search_item_description)

    fun bind(image: Image) {
        //Log.d(TAG, image.imageURL)
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(itemView.context)
            .load(image.imageUrl)
            .apply(requestOptions)
            .thumbnail(Glide.with(imageView.context).load(image.thumbnailUrl))
            .into(imageView)
        this.siteUrl.text = image.siteUrl
        this.descr.text = image.description
    }

}