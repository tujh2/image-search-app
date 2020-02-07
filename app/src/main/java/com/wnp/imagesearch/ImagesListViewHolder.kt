package com.wnp.imagesearch

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImagesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG = "Image View Holde"
    private val imageView: ImageView = itemView.findViewById(R.id.seatch_item_image)
    private val siteUrl: TextView = itemView.findViewById(R.id.search_item_link)
    private val descr: TextView = itemView.findViewById(R.id.search_item_description)

    fun bind(image: Image) {
        //Log.d(TAG, image.imageURL)
        Glide.with(itemView.context)
            .load(image.imageURL)
            .into(imageView)
        this.siteUrl.text = image.siteURl
        this.descr.text = image.description
    }

}